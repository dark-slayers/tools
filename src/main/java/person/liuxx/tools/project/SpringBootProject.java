package person.liuxx.tools.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import person.liuxx.util.file.DirUtil;
import person.liuxx.util.file.FileUtil;

/**
 * @author 刘湘湘
 * @version 1.0.0<br>
 *          创建时间：2018年3月30日 下午5:00:27
 * @since 1.0.0
 */
public class SpringBootProject
{
    private final Path projectPath;
    private final Path templatePath;
    private final String packagePath;
    private Map<String, String> map;

    public SpringBootProject(Path projectPath, String packagePath, String schenmaName,
            Path templatePath)
    {
        this.projectPath = projectPath;
        this.templatePath = templatePath;
        this.packagePath = packagePath;
        map = new HashMap<>();
        map.put("projectName", projectPath.getFileName().toString());
        map.put("schenmaName", schenmaName);
        map.put("packagePath", packagePath);
    }

    public void update() throws IOException
    {
        addReadmeFile();
        updateGitignore();
        updateLogXML();
        updatePOM();
        updateApplicationProperties();
        addConfigClass();
        updateLogo();
        copyIndexHtml();
    }

    /**
     * @author 刘湘湘
     * @version 1.0.0<br>
     *          创建时间：2018年1月2日 上午9:31:02
     * @throws IOException
     * @since 1.0.0
     */
    private void copyIndexHtml() throws IOException
    {
        Path indexTemplate = templatePath.resolve("index.html");
        Path indexTargetPath = projectPath.resolve("src/main/resources/static/index.html");
        if (FileUtil.existsFile(indexTargetPath))
        {
            return;
        }
        copyAndChange(indexTemplate, indexTargetPath);
    }

    private void addReadmeFile() throws IOException
    {
        Path readmeFile = projectPath.resolve("README.md");
        if (FileUtil.existsFile(readmeFile))
        {
            return;
        }
        String projectName = projectPath.getFileName().toString();
        List<String> lines = new ArrayList<>();
        lines.add("# " + projectName + "项目说明");
        Files.write(readmeFile, lines);
    }

    private void updateGitignore() throws IOException
    {
        Path gitignoreFile = projectPath.resolve(".gitignore");
        List<String> lines = Files.lines(gitignoreFile).map(l ->
        {
            String t = l.trim();
            switch (t)
            {
            case ".settings":
                {
                    return "";
                }
            case ".classpath":
                {
                    return "";
                }
            case ".project":
                {
                    return "";
                }
            default:
                {
                    return l;
                }
            }
        }).collect(Collectors.toList());
        if (!lines.contains("src/main/resources/static/views/"))
        {
            lines.add(1, "src/main/resources/static/views/");
        }
        Files.write(gitignoreFile, lines);
    }

    private void updateLogXML() throws IOException
    {
        Path logTemplate = templatePath.resolve("log4j2-dev.xml");
        Path logTargetPath = projectPath.resolve("src/main/resources/log4j2-dev.xml");
        if (FileUtil.existsFile(logTargetPath))
        {
            return;
        }
        copyAndChange(logTemplate, logTargetPath);
        logTemplate = templatePath.resolve("log4j2-prod.xml");
        logTargetPath = projectPath.resolve("src/main/resources/log4j2-prod.xml");
        if (FileUtil.existsFile(logTargetPath))
        {
            return;
        }
        copyAndChange(logTemplate, logTargetPath);
    }

    private void updatePOM() throws IOException
    {
        List<String> dependencieList = Files.readAllLines(templatePath.resolve("pom.xml"));
        List<String> pomList = Files.readAllLines(projectPath.resolve("pom.xml"));
        if (pomList.stream().anyMatch(l -> l.contains("<swagger.version>")))
        {
            return;
        }
        boolean remove = false;
        List<Integer> removeSet = new ArrayList<>();
        for (int i = 0, max = pomList.size(); i < max; i++)
        {
            String l = pomList.get(i).trim();
            if (Objects.equals("<properties>", l))
            {
                remove = true;
            }
            if (Objects.equals("<dependencies>", l))
            {
                remove = true;
            }
            if (remove)
            {
                removeSet.add(i);
            }
            if (Objects.equals("</properties>", l))
            {
                remove = false;
            }
            if (Objects.equals("</dependencies>", l))
            {
                remove = false;
            }
        }
        removeSet.sort((i1, i2) -> Integer.compare(i2, i1));
        for (int i : removeSet)
        {
            pomList.remove(i);
        }
        for (int i = 0, max = pomList.size(); i < max; i++)
        {
            String l = pomList.get(i).trim();
            if (Objects.equals("</parent>", l))
            {
                pomList.addAll(i + 1, dependencieList);
            }
        }
        Files.write(projectPath.resolve("pom.xml"), pomList);
    }

    private void updateApplicationProperties() throws IOException
    {
        Path targetDir = projectPath.resolve("src/main/resources");
        if (FileUtil.existsFile(targetDir.resolve("application-dev.properties")))
        {
            return;
        }
        List<String> lines = Files.readAllLines(templatePath.resolve("application.properties"));
        Files.write(targetDir.resolve("application.properties"), lines);
        copyAndChange(templatePath.resolve("application-dev.properties"), targetDir.resolve(
                "application-dev.properties"));
        copyAndChange(templatePath.resolve("application-prod.properties"), targetDir.resolve(
                "application-prod.properties"));
        DirUtil.copy(templatePath.resolve("META-INF"), targetDir.resolve("META-INF"));
    }

    private void addConfigClass() throws IOException
    {
        Path tempDir = templatePath.resolve("config");
        Path targetDir = projectPath.resolve("src/main/java")
                .resolve(packagePath.replace(".", "/"))
                .resolve("config");
        if (FileUtil.existsFile(targetDir.resolve("SwaggerConfig.java")))
        {
            return;
        }
        if (!DirUtil.exists(targetDir))
        {
            Files.createDirectories(targetDir);
        }
        copyAndChange(tempDir.resolve("SwaggerConfig.java"), targetDir.resolve(
                "SwaggerConfig.java"));
        copyAndChange(tempDir.resolve("InitConfig.java"), targetDir.resolve("InitConfig.java"));
        copyAndChange(tempDir.resolve("WebMvcConfig.java"), targetDir.resolve("WebMvcConfig.java"));
    }

    private void updateLogo()
    {
    }

    private void copyAndChange(Path source, Path target) throws IOException
    {
        copyAndChange(source, target, l ->
        {
            if (l.contains("${"))
            {
                Pattern pattern = Pattern.compile("\\$\\{\\w+?\\}");
                Matcher matcher = pattern.matcher(l);
                if (matcher.find())
                {
                    String matcherText = matcher.group(0);
                    String key = matcherText.substring(2, matcherText.length() - 1);
                    if (map.containsKey(key))
                    {
                        return l.replace("${" + key + "}", map.get(key));
                    }
                }
            }
            return l;
        });
    }

    private void copyAndChange(Path source, Path target, Function<String, String> mapper)
            throws IOException
    {
        List<String> lines = Files.lines(source).map(mapper).collect(Collectors.toList());
        Files.write(target, lines);
    }
}
