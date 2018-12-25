package person.liuxx.tools.dto;

import java.nio.file.Path;
import java.nio.file.Paths;

import person.liuxx.tools.project.SpringBootProject;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年4月17日 下午2:11:07
 * @since 1.0.0
 */
public class SpringBootProjectDTO
{
    private String sqlName;
    private String path;
    private String packagePath;

    public SpringBootProject mapToProject(Path templatePath)
    {
        return new SpringBootProject(Paths.get(path), packagePath, sqlName, templatePath);
    }

    public String getSqlName()
    {
        return sqlName;
    }

    public void setSqlName(String sqlName)
    {
        this.sqlName = sqlName;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getPackagePath()
    {
        return packagePath;
    }

    public void setPackagePath(String packagePath)
    {
        this.packagePath = packagePath;
    }

    @Override
    public String toString()
    {
        return "SpringBootProjectDTO [sqlName=" + sqlName + ", path=" + path + ", packagePath="
                + packagePath + "]";
    }
}
