/*
 * Copyright 2016 Goblom.
 * 
 * All Rights Reserved unless otherwise explicitly stated.
 */
package codes.goblom.connect.services.twilio;

import codes.goblom.connect.api.Contact;
import codes.goblom.connect.api.RequiredService;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Goblom
 */
@RequiredService( TwilioService.class )
public class PhoneNumber extends Contact {
    
    @Getter @Setter
    private int countryCode, areaCode, number;
    
    private final String strNum;
    
    public PhoneNumber(String stringNumber) {
        super();
        
        this.strNum = stringNumber.startsWith("+") ? stringNumber : "+" + stringNumber;
        
        this.countryCode = -1;
        this.areaCode = 0;
        this.number = 0;
    }
    
    /**
     * By default country code is 1 which is United States
     */
    public PhoneNumber(int areaCode, int number) {
        this(1, areaCode, number);
    }
    
    public PhoneNumber(int countryCode, int areaCode, int number) {
        super();
        
        this.countryCode = countryCode;
        this.areaCode = areaCode;
        this.number = number;
        
        this.strNum = null;
    }
    
    public boolean isStringNumber() {
        return strNum != null;
    }
    
    public String toNumberString() {
        if (isStringNumber()) {
            return strNum;
        }
        
        return new StringBuilder("+").append(countryCode <= 0 ? "" : countryCode).append(areaCode).append(number).toString();
    }
    
    @Override
    public String parse() {
        return toNumberString();
    }
    /**
     * Need to filter out all non-number characters otherwise it will error, will get to it later
     * @todo
     */
    public static PhoneNumber fromString(String str) {
        str = str.replace("-", "");
        str = str.replace(" ", "");
        
        int length = str.length();
        char[] chars = str.toCharArray();
        
        if (str.startsWith("+")) {
            return new PhoneNumber(str.startsWith("+") ? str : "+" + str);
        } else if (length <= 7) {
            throw new RuntimeException("Missing Critical Information. Make sure you have area code + phone number");
        } else if (length == 10) {
            int areaCode = Integer.valueOf("" + chars[0] + chars[1] + chars[2]);
            StringBuilder code = new StringBuilder();
            
            for (int i = 3; i < chars.length; i++) {
                code.append(chars[i]);
            }
            
            int number = Integer.valueOf(code.toString());
            
            return new PhoneNumber(areaCode, number);
        } else {            
            return new PhoneNumber(str.startsWith("+") ? str : "+" + str);
        }
    }
}
