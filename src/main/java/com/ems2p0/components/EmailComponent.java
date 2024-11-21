package com.ems2p0.components;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.ems2p0.enums.SecurityRoleProperties;

import java.io.UnsupportedEncodingException;

/**
 * EMS 2.0 - Service to trigger the email by using Spring javax email service by
 * configuring the mail smtp port , username and encoded password here we're
 * using dynamic sender email from properties files.
 *
 * @author Mohanlal
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailComponent {

	@Value("${spring.mail.from.email}")
	private String fromEmail;

	private final JavaMailSender mailSender;

	
	public void sendEmail(String email, String employeeName, Integer otp)
			throws UnsupportedEncodingException, MessagingException {
		String emailBody = "<html><body>" + "<p>Dear " + employeeName + ",</p>"
				+ "<p>Your One-time Password is - <strong>" + otp + "</strong>. Kindly use this for authentication.</p>"
				+ "<br><br>" + "<p>Regards,<br>" + "employee Covai</p>" + "</body></html>";
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(fromEmail, SecurityRoleProperties.FROM_CONTENT.getMessage());
		helper.setTo(email);
		helper.setSubject(SecurityRoleProperties.MAIL_SUBJECT.getMessage());
		helper.setText(emailBody, true);
		mailSender.send(message);
		log.info("Mail send successfully");
	}
}
