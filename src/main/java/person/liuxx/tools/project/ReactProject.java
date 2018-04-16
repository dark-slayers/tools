package person.liuxx.tools.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import person.liuxx.util.base.StringUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年4月13日 下午2:05:48
 * @since 1.0.0
 */
public class ReactProject extends AbstractZipProject
{
    private final String LF = new String(Character.toChars(0x0A));
    private final String projectName;
    private final Path path;
    private final String description;
    private final String license;

    public ReactProject(String projectName, String description, Path path, String license)
    {
        this.projectName = projectName;
        this.description = description;
        this.path = path;
        this.license = license;
    }

    public ReactProject(String projectName, String description, Path path)
    {
        this(projectName, description, path, "MIT");
    }

    private InputStream readmeInputStream() throws IOException
    {
        String readme = "# " + projectName + "\n" + description;
        return new ByteArrayInputStream(readme.getBytes("UTF-8"));
    }

    private InputStream packageJsonInputStream() throws IOException
    {
        String packageJson = Files.lines(path.resolve("package.json"))
                .filter(line -> !StringUtil.isBlank(line))
                .map(line ->
                {
                    if (line.contains("name\":"))
                    {
                        int index = line.indexOf("\": \"") + 4;
                        return line.substring(0, index) + projectName + "\",";
                    }
                    if (line.contains("description\":"))
                    {
                        int index = line.indexOf("\": \"") + 4;
                        return line.substring(0, index) + description + "\",";
                    }
                    if (line.contains("license\":"))
                    {
                        int index = line.indexOf("\": \"") + 4;
                        return line.substring(0, index) + license + "\"";
                    }
                    return line;
                })
                .collect(Collectors.joining(LF));
        return new ByteArrayInputStream(packageJson.getBytes("UTF-8"));
    }

    @Override
    protected InputStream createInputStream(Path p) throws IOException
    {
        switch (p.getFileName().toString())
        {
        case "package.json":
            {
                return packageJsonInputStream();
            }
        case "README.md":
            {
                return readmeInputStream();
            }
        }
        return Files.newInputStream(p);
    }

    @Override
    protected Path path()
    {
        return path;
    }
}
