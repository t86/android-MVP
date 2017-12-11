package com.mvp.common.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tanglin on 2017/7/20.
 */

public class StringUtils {

    public static String getStringWithEmpty(String origin) {
        if (TextUtils.isEmpty(origin)) {
            return "";
        } else {
            return origin;
        }
    }

    /**********************************************
     * 正则表达式相关
     **********************************************/
    public static final String REG_CELLPHONE = "^1[3,4,5,7,8]\\d{9}$";
    public static final String REG_CKYLPHONE = "^((\\+?86)|(\\(\\+86\\)))?1[3,4,5,7,8]\\d{9}$";
    public static final String REG_DIGITAL = "^[0-9]*$";
    public static final String REG_LETTER = "^[a-zA-Z]*$";
    public static final String REG_SAME_WORDS = "^(.)\\1*$";
    public static final String REG_VALID_WORDS = "^[a-zA-Z0-9!@#$%^&*_-]*$";
    public static final String REG_PASSWORD = "^[a-zA-Z0-9]*$";
    public static final String REG_PASSWORD2 = "^[a-zA-Z0-9!@#$%^&*_-]{6,12}$";
    public static final String REG_N = "[\u4E00-\u9FFF]+$";   //全是汉字 //龥
    public static final String REG_CHINESE_NAME = "^[\u4e00-\u9fa5]+$";
    public static final String REG_NAME = "^([\\u4E00-\\u9FA5]|\\w)*$";
    public static final String REG_CHINESE_AT_LEAST_ONE = "^.*[\u4e00-\u9fa5].*$"; //至少包含一个汉字
    public static final String REG_IDENTITY = "(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
    public static final String REG_EMAIL = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
    public static final String REG_TKYEPHONE = "^(0[0-9]{2,4}-)([2-9][0-9]{6,7})+(-[0-9]{1,5})?$"; //座机，带区号，带分机号（非必要）
    public static final String REG_TKYEPHONE_AREA_CODE = "^0[0-9]{2,3}$"; //区号
    public static final String REG_TKYEPHONE_PHONE_ONLY = "^[2-9][0-9]{6,7}$";  //座机，不带区号和分机号
    public static final String REG_TKYEPHONE_EXTENSION_NUMBER = "^[0-9]{1,6}$"; //分机号
    public static final String REG_HUNDREDNUM = "^[1-9]\\d*00$";
    public static final String[] ADDRESS_KEY_WORDS = new String[]{"号", "区", "路", "镇", "市", "村", "街", "县", "单元", "栋", "楼", "室", "巷", "组", "苑", "园", "里", "幢", "弄", "房", "座", "大厦", "户", "排", "屯", "广场", "中心"};


    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0 || string.equals("null");
    }

    public static boolean isEmail(String strEmail) {

        if (Pattern.matches(REG_EMAIL, strEmail))
            return true;
        else
            return false;
    }

    public static boolean isMobliePhone(String phone) {
        if (Pattern.matches(REG_CKYLPHONE, phone)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isChinaMobliePhone(String phone) {
        if (Pattern.matches(REG_CELLPHONE, phone)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTelephone(String phone) {
        if (Pattern.matches(REG_TKYEPHONE, phone)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTelephoneAreaCode(String phone) {
        if (Pattern.matches(REG_TKYEPHONE_AREA_CODE, phone)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTelephonePhoneNum(String phone) {
        if (Pattern.matches(REG_TKYEPHONE_PHONE_ONLY, phone)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTelephoneExtCode(String phone) {
        if (Pattern.matches(REG_TKYEPHONE_EXTENSION_NUMBER, phone)) {
            return true;
        } else {
            return false;
        }
    }

    // 是否是身份证号码
    public static boolean isIdentityCode(String id) {
        if (Pattern.matches(REG_IDENTITY, id)) {
            return true;
        } else {
            return false;
        }
    }

    // 是否是名字
    public static boolean isChineseName(String name) {
        if (Pattern.matches(REG_CHINESE_NAME, name)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNumeric(String strNumeric) {
        if (Pattern.matches(REG_DIGITAL, strNumeric))
            return true;
        else
            return false;
    }

    public static boolean isLetter(String str) {
        if (Pattern.matches(REG_LETTER, str))
            return true;
        else
            return false;
    }

    public static boolean hasSpChar(String str) {
        if (!Pattern.matches(REG_PASSWORD, str))
            return true;
        else
            return false;
    }

    public static boolean hasSpChar2(String str) {
        if (!Pattern.matches(REG_NAME, str))
            return true;
        else
            return false;
    }

    public static boolean isSameChar(String str) {
        if (Pattern.matches(REG_SAME_WORDS, str))
            return true;
        else
            return false;
    }

    public static boolean isDynamic(String dynamic) {
        return isNumeric(dynamic) && dynamic.length() == 6;
    }

    public static boolean hasKeyWord(String address) {
        int keyWordCount = 0;
        for (String keyWord : ADDRESS_KEY_WORDS) {
            if (address.contains(keyWord)) {
                keyWordCount++;
            }
        }
        return keyWordCount >= 2;
    }

    // 检测公司名
    public static boolean isCompanyName(String name) {
        if (Pattern.matches(REG_CHINESE_AT_LEAST_ONE, name)) {
            return true;
        } else {
            return false;
        }
    }

    public static String formatTelNum(String telNum) {
        if (telNum.startsWith("+86")) {
            return telNum.replace("+86", "");
        }
        if (telNum.startsWith("86")) {
            return telNum.replace("86", "");
        }
        return telNum;
    }

    /**
     * Determines if a "dial" event can be handled
     *
     * @param ctx the context in which the intent will be checekd
     * @return true if "dial" intents can be handled by the device
     */
    public static boolean checkTelephone(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_DIAL);
        List<ResolveInfo> list = pm.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    //格式化卡号，**************** -> **** **** **** ****
    public static String formatCardNum(String cardNumber, boolean isOnlyShowLastNum) {
        if (TextUtils.isEmpty(cardNumber)) {
            return null;
        }
        final String HIDDEN_CHAR = "**** ";
        final String SPLIT_CHAR = " ";
        final int SPLIT_LENGTH = 4;
        int numLines = Integer.parseInt(String.valueOf((int) Math.ceil((double) cardNumber.length() / (float) SPLIT_LENGTH)));
        String cardValues = "";
        for (int i = 0; i < numLines; i++) {
            if (i < numLines - 1) {
                if (isOnlyShowLastNum) {
                    cardValues += HIDDEN_CHAR;
                } else {
                    cardValues += cardNumber.substring(i * SPLIT_LENGTH, (i + 1) * SPLIT_LENGTH) + SPLIT_CHAR;
                }
            } else {
                cardValues += cardNumber.substring(i * SPLIT_LENGTH, cardNumber.length());
            }
        }
        return cardValues;
    }

    //格式化身份证号，**************** -> ****************2345
    public static String formatIdentityNum(String cardNumber) {
        if (TextUtils.isEmpty(cardNumber)) {
            return null;
        }
        final String HIDDEN_CHAR = "*";
        int numLines = cardNumber.length();
        String cardValues = "";
        for (int i = 0; i < numLines; i++) {
            if (i < numLines - 4) {
                cardValues += HIDDEN_CHAR;
            } else {
                cardValues += cardNumber.substring(i, cardNumber.length());
                break;
            }
        }
        return cardValues;
    }

    /**
     * 检查银行卡(Luhm校验)
     * 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
     * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，则将其减去9），再求和。
     * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
     *
     * @param cardNo
     * @return
     */
    public static boolean checkBankCardNo(String cardNo) {
        if (!isEmpty(cardNo)) {
            try {
                int luhmSum = 0;
                int num = 0;
                int index = 1;// 逆向后奇偶标志
                for (int i = cardNo.length() - 1; i >= 0; i--) {
                    num = Integer.parseInt(cardNo.charAt(i) + "");
                    if (index % 2 == 0) {
                        num = num * 2 > 9 ? num * 2 - 9 : num * 2;
                    }
                    luhmSum += num;
                    index++;
                }
                return luhmSum % 10 == 0;
            } catch (Exception ex) {
            }
        }
        return false;
    }

    /**
     * 检查身份证号码
     *
     * @param idCardNo
     * @return
     */
    public static boolean checkIdCardNo(String idCardNo) {
        if (!isEmpty(idCardNo)) {
            idCardNo = idCardNo.toUpperCase();
            int sum = 0;
            int[] tempNum = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
            String[] code = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
            if (idCardNo != null && idCardNo.length() == 18) {
                try {
                    for (int i = 0; i < 17; i++) {
                        sum += Integer.parseInt(idCardNo.substring(i, i + 1)) * tempNum[i];
                    }
                    if (idCardNo.substring(17, 18).equals(code[sum % 11])) {
                        return true;
                    }
                } catch (Exception e) {
                }
            }
        }
        return false;
    }
    public static boolean isNumberAndEngishString(String pwd) {
        /**
         * 是否只有数字和英文字母且都含有的组合
         */
        if (!TextUtils.isEmpty(pwd) && Pattern.matches("^[a-zA-Z0-9]*$", pwd)) {
            int english = 0, num = 0;
            for (char c : pwd.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    num++;
                } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                    english++;
                }
                if (num > 0 && english > 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
