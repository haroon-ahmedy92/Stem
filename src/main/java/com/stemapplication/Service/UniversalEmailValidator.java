package com.stemapplication.Service;

import org.springframework.stereotype.Service;
import java.net.IDN;
import java.util.regex.Pattern;

@Service
public class UniversalEmailValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[\\p{L}0-9!#$%&'*+\\/=?^_`{|}~.-]+@[\\p{L}0-9.-]+\\.[\\p{L}{2,}]+$"
    );

    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String domain = email.substring(email.lastIndexOf('@') + 1);
        String localPart = email.substring(0, email.lastIndexOf('@'));

        // Attempt to decode IDN domain
        try {
            domain = IDN.toUnicode(domain);
        } catch (IllegalArgumentException e) {
            // Invalid Punycode
            return false;
        }

        // Basic structural check (at least one dot in the domain)
        if (!domain.contains(".")) {
            return false;
        }

        // Use the regex pattern for validation
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
