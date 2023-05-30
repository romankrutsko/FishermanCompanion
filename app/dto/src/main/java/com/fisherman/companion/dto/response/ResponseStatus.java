package com.fisherman.companion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseStatus {
    LOGGED_OUT_SUCCESSFULLY("Вхід у систему успішний"),
    USER_RATED_SUCCESSFULLY("Користувача оцінено"),
    USER_DELETED_SUCCESSFULLY("Користувач був успішно видалений"),
    UNAUTHORIZED("Користувач не увійшов у систему"),
    WRONG_CREDENTIALS("Ви ввели неправильні дані для входу"),
    UNABLE_TO_GET_SETTLEMENTS("Не вдалося найти населені пункти, що містять дані літери"),
    UNABLE_TO_GET_SETTLEMENT_FROM_COORDINATES("Не вдалося найти населений пункт за координатами"),
    UNABLE_TO_GET_COORDINATES_FROM_SETTLEMENT("Не вдалося знайти координати населеного пункту"),
    UNABLE_TO_UPLOAD_FILE("Не вдалося загрузити файл"),
    USERNAME_IS_TAKEN("Дане імʼя користувача вже заняте");

    private final String code;
}
