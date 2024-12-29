package com.example.demo.usecase.common

import com.example.demo.config.MailConfig
import org.springframework.mail.MailSender
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component

@Component
class SendMail(
    private val mailSender: MailSender,
    private val mailConfig: MailConfig
) {
    fun send(mailAddress: String, subject: String, text: String) {
        mailSender.send(
            SimpleMailMessage().apply {
                this.from = mailConfig.from
                this.setTo(mailAddress)
                this.subject = subject
                this.text = text
            }
        )
    }
}