package utility;

import java.util.regex.Pattern;

/**
 * ValidationUtility - Centralized input validation
 * Author: [Your Name]
 */
public class ValidationUtility {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // Phone number pattern (flexible format)
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{3}-[0-9]{3}-[0-9]{4}$|^[0-9]{10}$");
    
    // University ID pattern (U followed by 6 digits)
    private static final Pattern UNIVERSITY_ID_PATTERN = 
        Pattern.compile("^U[0-9]{6}$");
    
    // ========== STRING VALIDATION ==========
    
    /**
     * Check if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Check if string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validate string length
     */
    public static boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Validate name (only letters, spaces, hyphens, apostrophes)
     */
    public static boolean isValidName(String name) {
        if (isNullOrEmpty(name)) {
            return false;
        }
        return name.matches("^[a-zA-Z'-\\s]+$");
    }
    
    // ========== EMAIL VALIDATION ==========
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    // ========== PHONE VALIDATION ==========
    
    /**
     * Validate phone number
     * Accepts formats: 555-123-4567 or 5551234567
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (isNullOrEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Format phone number to standard format (555-123-4567)
     */
    public static String formatPhoneNumber(String phone) {
        if (isNullOrEmpty(phone)) {
            return "";
        }
        
        // Remove all non-digit characters
        String digits = phone.replaceAll("[^0-9]", "");
        
        if (digits.length() == 10) {
            return String.format("%s-%s-%s", 
                digits.substring(0, 3),
                digits.substring(3, 6),
                digits.substring(6, 10));
        }
        
        return phone;
    }
    
    // ========== UNIVERSITY ID VALIDATION ==========
    
    /**
     * Validate university ID format (U123456)
     */
    public static boolean isValidUniversityId(String id) {
        if (isNullOrEmpty(id)) {
            return false;
        }
        return UNIVERSITY_ID_PATTERN.matcher(id.trim()).matches();
    }
    
    // ========== NUMERIC VALIDATION ==========
    
    /**
     * Check if string is a valid integer
     */
    public static boolean isValidInteger(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Check if string is a valid double
     */
    public static boolean isValidDouble(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        try {
            Double.parseDouble(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate positive integer
     */
    public static boolean isPositiveInteger(String str) {
        if (!isValidInteger(str)) {
            return false;
        }
        return Integer.parseInt(str.trim()) > 0;
    }
    
    /**
     * Validate non-negative integer
     */
    public static boolean isNonNegativeInteger(String str) {
        if (!isValidInteger(str)) {
            return false;
        }
        return Integer.parseInt(str.trim()) >= 0;
    }
    
    /**
     * Validate positive double
     */
    public static boolean isPositiveDouble(String str) {
        if (!isValidDouble(str)) {
            return false;
        }
        return Double.parseDouble(str.trim()) > 0;
    }
    
    /**
     * Validate non-negative double
     */
    public static boolean isNonNegativeDouble(String str) {
        if (!isValidDouble(str)) {
            return false;
        }
        return Double.parseDouble(str.trim()) >= 0;
    }
    
    /**
     * Validate number within range
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    // ========== GPA VALIDATION ==========
    
    /**
     * Validate GPA (0.0 to 4.0)
     */
    public static boolean isValidGPA(double gpa) {
        return gpa >= 0.0 && gpa <= 4.0;
    }
    
    /**
     * Validate GPA string
     */
    public static boolean isValidGPA(String gpaStr) {
        if (!isValidDouble(gpaStr)) {
            return false;
        }
        double gpa = Double.parseDouble(gpaStr.trim());
        return isValidGPA(gpa);
    }
    
    // ========== GRADE VALIDATION ==========
    
    /**
     * Validate letter grade
     */
    public static boolean isValidGrade(String grade) {
        if (isNullOrEmpty(grade)) {
            return false;
        }
        
        String[] validGrades = {"A", "A-", "B+", "B", "B-", "C+", "C", "C-", "F"};
        String gradeUpper = grade.trim().toUpperCase();
        
        for (String validGrade : validGrades) {
            if (validGrade.equals(gradeUpper)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Validate percentage score (0-100)
     */
    public static boolean isValidPercentage(double percentage) {
        return percentage >= 0.0 && percentage <= 100.0;
    }
    
    // ========== CREDIT HOURS VALIDATION ==========
    
    /**
     * Validate credit hours (typically 1-4 for university courses)
     */
    public static boolean isValidCreditHours(int credits) {
        return credits >= 1 && credits <= 8;
    }
    
    // ========== PASSWORD VALIDATION ==========
    
    /**
     * Validate password strength
     * At least 8 characters
     */
    public static boolean isValidPassword(String password) {
        if (isNullOrEmpty(password)) {
            return false;
        }
        return password.length() >= 8;
    }
    
    /**
     * Validate strong password
     * At least 8 characters, with uppercase, lowercase, and number
     */
    public static boolean isStrongPassword(String password) {
        if (!isValidPassword(password)) {
            return false;
        }
        
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        
        return hasUpper && hasLower && hasDigit;
    }
    
    // ========== USERNAME VALIDATION ==========
    
    /**
     * Validate username
     * 3-20 characters, alphanumeric and underscore only
     */
    public static boolean isValidUsername(String username) {
        if (isNullOrEmpty(username)) {
            return false;
        }
        
        String trimmed = username.trim();
        return trimmed.matches("^[a-zA-Z0-9_]{3,20}$");
    }
    
    // ========== DATE VALIDATION ==========
    
    /**
     * Validate year
     */
    public static boolean isValidYear(int year) {
        return year >= 2000 && year <= 2100;
    }
    
    /**
     * Validate semester term
     */
    public static boolean isValidSemesterTerm(String term) {
        if (isNullOrEmpty(term)) {
            return false;
        }
        
        String termUpper = term.trim().toUpperCase();
        return termUpper.equals("FALL") || 
               termUpper.equals("SPRING") || 
               termUpper.equals("SUMMER");
    }
    
    // ========== ROLE VALIDATION ==========
    
    /**
     * Validate user role
     */
    public static boolean isValidRole(String role) {
        if (isNullOrEmpty(role)) {
            return false;
        }
        
        String roleUpper = role.trim().toUpperCase();
        return roleUpper.equals("ADMIN") || 
               roleUpper.equals("FACULTY") || 
               roleUpper.equals("STUDENT") || 
               roleUpper.equals("REGISTRAR");
    }
    
    // ========== UTILITY METHODS ==========
    
    /**
     * Sanitize string input (remove leading/trailing spaces, prevent injection)
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return "";
        }
        
        // Remove leading/trailing spaces
        String sanitized = input.trim();
        
        // Remove potential SQL injection characters (basic protection)
        sanitized = sanitized.replaceAll("[';\"\\\\]", "");
        
        return sanitized;
    }
    
    /**
     * Capitalize first letter of each word
     */
    public static String capitalizeWords(String str) {
        if (isNullOrEmpty(str)) {
            return "";
        }
        
        String[] words = str.trim().split("\\s+");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }
        
        return result.toString().trim();
    }
    
    /**
     * Generate error message for invalid field
     */
    public static String generateErrorMessage(String fieldName, String requirement) {
        return fieldName + " " + requirement;
    }
}