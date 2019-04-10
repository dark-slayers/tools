package person.liuxx.tools.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import person.liuxx.util.log.LogUtil;
import person.liuxx.util.service.exception.BaseExceptionHandlerAdvice;
import person.liuxx.util.service.exception.ExcelParseException;
import person.liuxx.util.service.exception.SaveException;
import person.liuxx.util.service.reponse.ErrorResponse;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2017年10月25日 上午10:07:54
 * @since 1.0.0
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice extends BaseExceptionHandlerAdvice
{
    private Logger log = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(
    { Exception.class })
    public ErrorResponse exceptionHandler(Exception e)
    {
        log.error(LogUtil.errorInfo(e));
        String exceptionClassName = e.getClass().getName();
        List<String> exceptionClassNameList = getExceptionClassNameList();
        if (exceptionClassNameList.contains(exceptionClassName))
        {
            return baseExceptionHandler(e);
        } else
        {
            switch (exceptionClassName)
            {
            case "person.liuxx.util.service.exception.SaveException":
                {
                    SaveException e1 = (SaveException) e;
                    return new ErrorResponse(409, 40901, e1.getMessage(), "失败信息：" + LogUtil
                            .errorInfo(e), "more info");
                }
            case "person.liuxx.util.service.exception.ExcelParseException":
                {
                    ExcelParseException e1 = (ExcelParseException) e;
                    return new ErrorResponse(409, 40902, e1.getMessage(), "失败信息：" + LogUtil
                            .errorInfo(e), "more info");
                }
            default:
                {
                    return new ErrorResponse(500, 50001, "未知错误", "失败信息：" + LogUtil.errorInfo(e),
                            "more info");
                }
            }
        }
    }
}
