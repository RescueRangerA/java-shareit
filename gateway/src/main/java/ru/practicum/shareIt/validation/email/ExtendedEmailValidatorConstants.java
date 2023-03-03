package ru.practicum.shareIt.validation.email;

interface ExtendedEmailValidatorConstants {
    String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]";
    String DOMAIN = "(" + ATOM + "+(\\." + ATOM + "+)+";
    String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";

    String PATTERN =
            "^" + ATOM + "+(\\." + ATOM + "+)*@"
                    + DOMAIN
                    + "|"
                    + IP_DOMAIN
                    + ")$";
}
