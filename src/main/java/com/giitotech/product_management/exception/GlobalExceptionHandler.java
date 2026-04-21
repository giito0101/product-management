package com.giitotech.product_management.exception;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SessionNotFoundException.class)
    public String handleSessionNotFound(SessionNotFoundException ex,
                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/error/general";
    }

    // UserFormNotFoundExceptionを処理
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserFormNotFoundException.class)
    public ModelAndView handleUserFormNotFoundException(UserFormNotFoundException ex) {
        ModelAndView mav = new ModelAndView("product-management/error/general");
        String errorMessage = ex.getMessage() != null ? ex.getMessage() : "UserForm Not Found";

        // ログと画面にエラーメッセージを出力
        logger.error(errorMessage, ex);

        // エラーメッセージとHTTPステータスコードを含むレスポンスを返す
        mav.addObject("errorMessage", errorMessage);
        mav.addObject("status", HttpStatus.NOT_FOUND);

        return mav;
    }

    @ExceptionHandler(RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleRoleNotFoundException(RoleNotFoundException ex) {
        // 定数定義
        final String DEFAULT_MESSAGE = "Role Not Found";
        final String ERROR_VIEW = "product-management/error/general";

        ModelAndView mav = new ModelAndView(ERROR_VIEW);

        // エラーメッセージの設定（nullの場合はデフォルトメッセージを使用）
        String errorMessage = ex.getMessage();
        if (errorMessage == null) {
            errorMessage = DEFAULT_MESSAGE;
        }

        // ログ出力とエラー情報の設定
        logger.error(errorMessage, ex);
        mav.addObject("errorMessage", errorMessage);
        mav.addObject("status", HttpStatus.NOT_FOUND);

        return mav;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleUserNotFoundException(UserNotFoundException ex) {
        // 定数定義
        final String DEFAULT_MESSAGE = "User Not Found";
        final String ERROR_VIEW = "product-management/error/general";

        ModelAndView mav = new ModelAndView(ERROR_VIEW);

        // エラーメッセージの設定（nullの場合はデフォルトメッセージを使用）
        String errorMessage = ex.getMessage();
        if (errorMessage == null) {
            errorMessage = DEFAULT_MESSAGE;
        }

        // ログ出力とエラー情報の設定
        logger.error(errorMessage, ex);
        mav.addObject("errorMessage", errorMessage);
        mav.addObject("status", HttpStatus.NOT_FOUND);

        return mav;
    }

    // IOException のハンドリング
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleIOException(IOException ex) {
        String errorMessage = "Internal Server Error: I/O エラーが発生しました";
        logger.error(errorMessage, ex);

        ModelAndView mav = new ModelAndView("product-management/error/general");
        mav.addObject("errorMessage", errorMessage);
        mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }

    // ProductNotFoundExceptionを処理
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleIllegalArgumentException(ProductNotFoundException ex) {
        String errorMessage = "Product Not Found";

        // ログと画面にエラーメッセージを出力
        if (ex.getMessage() == null) {
            logger.error(errorMessage, ex);
        } else {
            logger.error(ex.getMessage(), ex);
            errorMessage = ex.getMessage();
        }

        ModelAndView mav = new ModelAndView("product-management/error/general");
        mav.addObject("errorMessage", errorMessage);
        mav.addObject("status", HttpStatus.NOT_FOUND);
        return mav;
    }

    // IllegalArgumentExceptionを処理
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleIllegalArgumentException(IllegalArgumentException ex) {
        String errorMessage = "Invalid argument provided: " + ex.getMessage();
        logger.error(errorMessage, ex);

        ModelAndView mav = new ModelAndView("product-management/error/general");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("status", HttpStatus.BAD_REQUEST);
        return mav;
    }

    // その他の例外を処理
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGeneralException(Exception ex) {
        String errorMessage = "An unexpected error occurred: " + ex.getMessage();
        logger.error(errorMessage, ex);

        ModelAndView mav = new ModelAndView("product-management/error/general");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR);
        return mav;
    }
}
