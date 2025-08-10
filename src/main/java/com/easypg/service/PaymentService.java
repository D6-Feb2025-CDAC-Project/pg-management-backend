package com.easypg.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.codec.digest.HmacUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.easypg.dao.PaymentDao;
import com.easypg.dao.TenantDao;
import com.easypg.dao.UserDao;
import com.easypg.dao.RoomDao;
import com.easypg.entities.BaseUser;
import com.easypg.entities.Payment;
import com.easypg.entities.Room;
import com.easypg.entities.Tenant;
import com.easypg.enums.PaymentMethod;
import com.easypg.enums.PaymentStatus;
import com.easypg.enums.PaymentType;
import com.easypg.enums.UserRole;
import com.razorpay.Order;
import com.razorpay.Refund;
import com.razorpay.RazorpayClient;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentService {
    
    @Value("${razorpay.key.secret}")
    private String keySecret;
    
    @Autowired
    private RazorpayClient razorpayClient;
    @Autowired
    private PaymentDao paymentDao;
    @Autowired
    private TenantDao tenantDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoomDao roomDao; 
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public String createOrder(double amount, String receipt) throws Exception {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt != null ? receipt : "reg_" + System.currentTimeMillis());
        
        Order order = razorpayClient.orders.create(orderRequest);
        return order.get("id");
    }
    
    public boolean verifyPayment(String orderId, String paymentId, String signature) {
        try {
            String generatedSignature = HmacUtils.hmacSha256Hex(keySecret, orderId + "|" + paymentId);
            return generatedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Payment verification failed", e);
            return false;
        }
    }
    
    public PaymentMethod detectPaymentMethod(String paymentId) {
        try {
            com.razorpay.Payment payment = razorpayClient.payments.fetch(paymentId);
            String method = payment.get("method").toString().toLowerCase();
            
            return switch (method) {
                case "upi" -> PaymentMethod.UPI;
                case "card" -> PaymentMethod.CARD;
                case "netbanking" -> PaymentMethod.NETBANKING;
                case "wallet" -> PaymentMethod.WALLET;
                default -> PaymentMethod.OTHER;
            };
        } catch (Exception e) {
            log.error("Failed to fetch payment method for paymentId: " + paymentId, e);
            return PaymentMethod.OTHER;
        }
    }
    
    @Transactional
    public Tenant createTenantWithPayment(String username, String email, String password, 
                                         String gender, String contactNumber, LocalDate moveInDate, 
                                         Long roomId, String orderId, String paymentId, 
                                         String signature, BigDecimal amount) throws Exception {
        
        // 1. Create BaseUser first
        BaseUser user = new BaseUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserRole(UserRole.ROLE_USER);
        user = userDao.save(user);
        
        // 2. Get Room
        Room room = roomDao.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));
        
        // 3. Create Tenant
        Tenant tenant = new Tenant();
        tenant.setUser(user); // This will set the same ID due to @MapsId
        tenant.setGender(gender);
        tenant.setContactNumber(contactNumber);
        tenant.setMoveInDate(moveInDate);
        tenant.setRoom(room);
        tenant = tenantDao.save(tenant);
        
        // 4. Update room occupancy
        room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
        if (room.getCurrentOccupancy() >= room.getMaxOccupancy()) {
            room.setAvailable(false);
        }
        roomDao.save(room);
        
        // 5. Create Payment record
        Payment payment = new Payment();
        payment.setTenant(tenant);
        payment.setPaymentAmount(amount);
        payment.setPaymentType(PaymentType.SECURITY_DEPOSIT); // Using for registration
        payment.setPaymentMethod(detectPaymentMethod(paymentId));
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setRazorpayOrderId(orderId);
        payment.setRazorpayPaymentId(paymentId);
        payment.setRazorpaySignature(signature);
        payment.setAdditionalComments("Registration payment via Razorpay");
        payment.setCreatedBy(email);
        paymentDao.save(payment);
        
        log.info("Tenant registration completed successfully for email: {}", email);
        return tenant;
    }
    
    public boolean isPaymentAlreadyProcessed(String paymentId) {
        // Use the cleaner method if you add it to DAO, otherwise:
        return paymentDao.findByRazorpayPaymentId(paymentId).isPresent();
    }
    
    public boolean hasUserAlreadyRegistered(String email) {
        return userDao.existsByEmail(email);
    }
    
    public Optional<Payment> getSecurityDepositPayment(Tenant tenant) {
        return paymentDao.findByTenantAndPaymentTypeAndPaymentStatus(
            tenant, PaymentType.SECURITY_DEPOSIT, PaymentStatus.SUCCESS
        );
    }
    
    // For future refund functionality when tenant leaves
    public String initiateRefund(Payment securityPayment, BigDecimal refundAmount, String reason) throws Exception {
        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", refundAmount.multiply(new BigDecimal(100)).intValue()); // paise
        refundRequest.put("notes", new JSONObject().put("reason", reason));
        
        Refund refund = razorpayClient.payments.refund(securityPayment.getRazorpayPaymentId(), refundRequest);
        return refund.get("id");
    }
    
    public Optional<Payment> findByOrderId(String orderId) {
        return paymentDao.findByRazorpayOrderId(orderId);
    }
}