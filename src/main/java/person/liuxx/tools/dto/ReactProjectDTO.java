package person.liuxx.tools.dto;

import java.nio.file.Path;

import person.liuxx.tools.project.ReactProject;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年4月17日 下午2:11:07
 * @since 1.0.0
 */
public class ReactProjectDTO
{
    private String projectName;
    private String description;
    private String license;

    public ReactProject mapToProject(Path path)
    {
        return new ReactProject(projectName, description, path, license);
    }

    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }

    @Override
    public String toString()
    {
        return "ReactProjectDTO [projectName=" + projectName + ", description=" + description
                + ", license=" + license + "]";
    }
}
