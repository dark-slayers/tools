package person.liuxx.tools.project;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年3月30日 下午5:00:27
 * @since 1.0.0
 */
public class SpringBootProject extends AbstractZipProject
{
    private Path path;
    private Path rootPath;
    private String projectName;
    private String sqlName;
    private String packagePath;
    private String indexHtml;

    public Path getRootPath()
    {
        return rootPath;
    }

    public void setRootPath(Path rootPath)
    {
        this.rootPath = rootPath;
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getSqlName()
    {
        return sqlName;
    }

    public void setSqlName(String sqlName)
    {
        this.sqlName = sqlName;
    }

    public String getPackagePath()
    {
        return packagePath;
    }

    public void setPackagePath(String packagePath)
    {
        this.packagePath = packagePath;
    }

    public String getIndexHtml()
    {
        return indexHtml;
    }

    public void setIndexHtml(String indexHtml)
    {
        this.indexHtml = indexHtml;
    }

    @Override
    protected InputStream createInputStream(Path p) throws IOException
    {
        // TODO 自动生成的方法存根
        return null;
    }

    @Override
    protected Path path()
    {

        return path;
    }
}
