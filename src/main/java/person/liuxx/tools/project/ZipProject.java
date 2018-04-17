package person.liuxx.tools.project;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年4月17日 下午4:41:05
 * @since 1.0.0
 */
public interface ZipProject
{
    /** 获取项目使用zip打包后的zip流
    * @author  刘湘湘 
    * @version 1.0.0<br>创建时间：2018年4月17日 下午4:47:59
    * @since 1.0.0 
    * @return
    * @throws IOException
    */
    ByteArrayOutputStream createZipOutputStream() throws IOException;

    /** 获取项目名称
    * @author  刘湘湘 
    * @version 1.0.0<br>创建时间：2018年4月17日 下午4:48:03
    * @since 1.0.0 
    * @return
    */
    String getProjectName();
}
