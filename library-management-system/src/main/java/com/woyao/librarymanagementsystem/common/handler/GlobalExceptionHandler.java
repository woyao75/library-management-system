package com.woyao.librarymanagementsystem.common.handler;

import com.woyao.librarymanagementsystem.common.exception.BusinessException;
import com.woyao.librarymanagementsystem.common.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;

import java.util.stream.Collectors;


/**
 * 全局异常处理器 @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 * 表示拦截异常后，直接将返回对象序列化为 JSON
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义的业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?>  handleBusinessException(BusinessException e){
        return Result.error(e.getCode(),e.getMessage());
    }

    /**
     * 处理 @RequestBody 参数校验异常(@Valid/@Validated)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        // 获取第一个校验失败的信息
        String message = e.getBindingResult()
                        .getAllErrors()
                        .get(0)
                        .getDefaultMessage();

        // 获取全部失败信息
        // String message = e.getBindingResult()
        //                   .getFieldError()
        //                   .stream()
        //                   .map(this::formatFieldError)
        //                   .collect(Collectors.joining("；"));
//        private String formatFieldError(FieldError fieldError) {
//            return fieldError.getField()
//                    + ": "
//                    + fieldError.getDefaultMessage();
//        }
        return Result.error(400,message);
    }

    /**
     * 处理 @ModelAttribute / 表单提交 参数校验异常
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e){
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.error(400,message);
    }

    /**
     *处理前端传入单个散装参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> handleConstraintViolationException(ConstraintViolationException e){
        // 获取第一个校验失败的信息
        String message = e.getConstraintViolations()
                .stream().findFirst().map(ConstraintViolation::getMessage).orElse("参数校验失败");

        // 获取全部失败信息
        // String message = e.getConstraintViolations()
        //                   .stream()
        //                   .map(item -> item.getMessage())
        //                   .collect(Collectors.joining("；"));
        return Result.error(400,message);
    }

    /**
     * 处理请求参数格式异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        return Result.error(400,"请求参数格式错误");
    }

    /**
     * 兜底异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e){
        log.error("系统异常",e);
        return Result.error(500,"系统异常，请稍后再试");
    }
}
