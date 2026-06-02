package com.CongNgheJave.ecommerce_system.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String FROM_EMAIL = "thachbao2910@gmail.com";
    private static final String FROM_NAME = "ShoeStore";

    private final JavaMailSender mailSender;

    public boolean sendResetPasswordEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    false,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME, StandardCharsets.UTF_8.name()));
            helper.setTo(toEmail);
            helper.setReplyTo(FROM_EMAIL);
            helper.setSubject("Yêu cầu đặt lại mật khẩu | ShoeStore");
            helper.setText(buildResetPasswordHtml(resetLink), true);

            mailSender.send(message);
            System.out.println("====== [EMAIL DISPATCHED SUCCESSFULLY] To: " + toEmail + " ======");
            return true;
        } catch (Exception e) {
            System.err.println("====== [EMAIL SENDING FAILED] SMTP error: " + e.getMessage());
            System.out.println("====== [RESET PASSWORD LINK (FALLBACK LOG)] ======");
            System.out.println("Gui toi email: " + toEmail);
            System.out.println("Lien ket khoi phuc: " + resetLink);
            System.out.println("==================================================");
            return false;
        }
    }

    private String buildResetPasswordHtml(String resetLink) {
        return """
                <div style="margin:0;padding:24px;background:#f4f6f8;font-family:Arial,sans-serif;color:#1f2937;">
                  <div style="max-width:560px;margin:0 auto;background:#ffffff;border:1px solid #e5e7eb;border-radius:16px;overflow:hidden;">
                    <div style="padding:24px 28px;background:#111827;color:#ffffff;">
                      <div style="font-size:24px;font-weight:700;letter-spacing:0.5px;">ShoeStore</div>
                      <div style="margin-top:8px;font-size:14px;color:#d1d5db;">Yêu cầu đặt lại mật khẩu</div>
                    </div>
                    <div style="padding:28px;">
                      <p style="margin:0 0 16px;">Xin chào,</p>
                      <p style="margin:0 0 16px;line-height:1.7;">
                        Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn tại ShoeStore.
                      </p>
                      <p style="margin:0 0 24px;line-height:1.7;">
                        Nhấn vào nút dưới đây để đặt lại mật khẩu. Liên kết này có hiệu lực trong <strong>15 phút</strong>.
                      </p>
                      <div style="margin:0 0 24px;">
                        <a href="%s" style="display:inline-block;padding:12px 20px;background:#f59e0b;color:#111827;text-decoration:none;border-radius:10px;font-weight:700;">
                          Đặt lại mật khẩu
                        </a>
                      </div>
                      <p style="margin:0 0 12px;line-height:1.7;">Nếu nút không mở được, sao chép liên kết sau vào trình duyệt:</p>
                      <p style="margin:0 0 24px;word-break:break-all;">
                        <a href="%s" style="color:#2563eb;text-decoration:none;">%s</a>
                      </p>
                      <p style="margin:0 0 16px;line-height:1.7;">
                        Nếu bạn không thực hiện yêu cầu này, bạn có thể bỏ qua email này.
                      </p>
                      <p style="margin:0;line-height:1.7;">
                        Trân trọng,<br>ShoeStore
                      </p>
                    </div>
                  </div>
                </div>
                """.formatted(resetLink, resetLink, resetLink);
    }
}
