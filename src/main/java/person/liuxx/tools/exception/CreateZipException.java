package person.liuxx.tools.exception;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年4月17日 下午2:19:35
 * @since 1.0.0
 */
public class CreateZipException extends RuntimeException
{
    private static final long serialVersionUID = 2007245600506130159L;

    public CreateZipException(String message)
    {
        super(message);
    }

    public CreateZipException(String message, Throwable e)
    {
        super(message, e);
    }
}
