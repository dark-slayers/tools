package person.liuxx.tools.project;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import person.liuxx.util.file.FileUtil;

/**
 * @author 刘湘湘
 * @since 2019年4月10日 下午5:38:20
 */
public class SpringBootProjectTest
{
    Path templatePath = Paths.get(
            "E:/GitProject/tools/src/main/resources/libs/project/java/springboot");
    Path projectPath = Paths.get("E:/dshell/news");
    final String PATH_RESOURCES = "src/main/resources/";
    final String PATH_JAVA = "src/main/java/";
    final String PATH_STATIC = "src/main/resources/static/";
    SpringBootProject project;

    /**
     * @author 刘湘湘
     * @since 2019年4月10日 下午5:38:20
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        project = new SpringBootProject(projectPath, "person.liuxx.news", "news", templatePath);
        Path applicationPath = projectPath.resolve(PATH_RESOURCES).resolve(
                "application.properties");
        if (FileUtil.existsFile(applicationPath))
        {
            Files.delete(applicationPath);
        }
    }

    /**
     * @author 刘湘湘
     * @since 2019年4月10日 下午5:38:20
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    /**
     * {@link person.liuxx.tools.project.SpringBootProject#checkAndCopy(java.nio.file.Path, java.nio.file.Path, String)}
     * 的测试方法。
     */
    @Test
    public void testCheckAndCopy()
    {
        Path targetDir = projectPath.resolve(PATH_RESOURCES);
        try
        {
            boolean successed = project.checkAndCopy(templatePath, targetDir,
                    "application.properties");
            assertTrue(successed);
        } catch (IOException e)
        {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

    /**
     * {@link person.liuxx.tools.project.SpringBootProject#updatePOM()} 的测试方法。
     */
    @Test
    public void testUpdatePOM()
    {
        try
        {
            project.updatePOM();
        } catch (IOException e)
        {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }
}
