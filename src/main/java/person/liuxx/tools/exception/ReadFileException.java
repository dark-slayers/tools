package person.liuxx.tools.exception;

/**
 * @author 刘湘湘
 * @since 2019年4月10日 下午5:58:46
 */
public class ReadFileException extends RuntimeException
{
    private static final long serialVersionUID = -7323783241327756344L;

    public ReadFileException(String message)
    {
        super(message);
    }

    public ReadFileException(String message, Throwable e)
    {
        super(message, e);
    }
}
