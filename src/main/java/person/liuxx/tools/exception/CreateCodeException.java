package person.liuxx.tools.exception;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年4月17日 下午2:19:35
 * @since 1.0.0
 */
public class CreateCodeException extends RuntimeException
{
    private static final long serialVersionUID = 6485200758177370297L;

    public CreateCodeException(String message)
    {
        super(message);
    }

    public CreateCodeException(String message, Throwable e)
    {
        super(message, e);
    }
}
