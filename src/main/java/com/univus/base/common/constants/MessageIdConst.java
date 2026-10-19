package com.univus.base.common.constants;

import lombok.NoArgsConstructor;

/**
 * メッセージプロパティキー定義
 *
 * @author Univus
 */
@NoArgsConstructor
public class MessageIdConst {

    // Info Messages
    public static final String I_SERVER_RUNNING = "info.server.running";
    public static final String I_GETTING_SUCCESS = "info.getting.success";
    public static final String I_INSERT_SUCCESS = "info.insert.success";
    public static final String I_UPDATE_SUCCESS = "info.update.success";
    public static final String I_DELETE_SUCCESS = "info.delete.success";
    public static final String I_SAVE_SUCCESS = "info.save.success";
    public static final String I_LOGIN_SUCCESS = "info.loginIn";
    public static final String I_LOGOUT_SUCCESS = "info.loginOut";
    public static final String I_TOKEN_VERIFIED = "info.verifyToken";
    public static final String I_TOKEN_REFRESHED = "info.refreshToken";
    public static final String I_ALREADY_EXIST = "info.alreadyExist";

    // Error Validation Messages
    public static final String E_INVALID_TERM = "error.invalidterm";
    public static final String E_DATE_FORMAT = "error.date.format";
    public static final String E_BAD_REQUEST = "error.badRequest";
    public static final String E_UPLOAD_MAX_SIZE = "error.upload.maxSize";
    public static final String E_WEB_NOT_FOUND = "error.web.notFound";

    // Database Messages
    public static final String E_DATA_NOT_FOUND = "error.dataNotFound";
    public static final String E_DATA_NOT_FOUND_ON_MASTER = "error.dataNotFoundOnMaster";
    public static final String E_USER_ALREADY_REGISTER = "error.userAlreadyRegister";
    public static final String E_USER_NOT_REGISTER = "error.userNotRegister";
    public static final String E_SQL_ISSUE = "error.sql.issue";

    // System Messages
    public static final String E_SYSTEM = "error.system";

    // JWT & Auth Messages
    public static final String E_NO_ACCESS_TOKEN = "error.noAccessToken";
    public static final String E_EXPIRED_TOKEN = "error.expiredJWT";
    public static final String E_INVALID_TOKEN = "error.invalidJWT";
    public static final String E_UNSUPPORTED_TOKEN = "error.unsupportedJWT";
    public static final String E_ILLEGAL_TOKEN = "error.illegalJWT";
    public static final String E_NO_REFRESH_TOKEN = "error.noRefreshToken";
    public static final String E_INVALID_ROLE = "error.invalidRole";
    public static final String E_LOGIN_USER_FAILED = "login.user.failed";
}
