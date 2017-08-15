package motocitizen.dictionary;

public enum AccidentStatus {
    /*
                            WHEN "acc_status_act" THEN "a"
                        WHEN "acc_status_dbl" THEN "d"
                        WHEN "acc_status_end"  THEN "e"
                        WHEN "acc_status_hide" THEN "h"
                        WHEN "acc_status_war" THEN "w"
     */
    ACTIVE("a", "Активный"),
    ENDED("e", "Отбой"),
    HIDDEN("h", "Скрыт"),
    CONFLICT("w", "Конфликт"),
    DUPLICATE("d", "Дубль");
//    ACTIVE("acc_status_act", "Активный"),
//    ENDED("acc_status_end", "Отбой"),
//    HIDDEN("acc_status_hide", "Скрыт"),
//    CONFLICT("acc_status_war", "Конфликт"),
//    DUPLICATE("acc_status_dbl", "Дубль");

    private final String code;
    private final String text;

    AccidentStatus(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public static AccidentStatus parse(String status) {
        for (AccidentStatus a : AccidentStatus.values()) {
            if (a.code.equals(status)) return a;
        }
        return AccidentStatus.ACTIVE;
    }

    public String code() {
        return this.code;
    }

    public String string() {
        return this.text;
    }
}