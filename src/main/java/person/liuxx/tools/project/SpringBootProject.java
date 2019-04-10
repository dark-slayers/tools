package person.liuxx.tools.project;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import person.liuxx.util.file.DirUtil;
import person.liuxx.util.file.FileUtil;

public class SpringBootProject
{
    private final Path projectPath;
    private final Path templatePath;
    private final String packagePath;
    private Map<String, String> map;
    private static final String PATH_RESOURCES = "src/main/resources/";
    private static final String PATH_JAVA = "src/main/java/";
    private static final String PATH_STATIC = "src/main/resources/static/";

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

    private void copyIndexHtml() throws IOException
    {
        checkAndCopy(templatePath, projectPath.resolve(PATH_STATIC), "index.html");
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
        Set<String> needReplaceBlankSet = Arrays.stream(new String[]
        { ".settings", ".classpath", ".project" }).collect(Collectors.toSet());
        List<String> lines = Files.lines(gitignoreFile)
                .map(l -> l.trim())
                .map(t -> needReplaceBlankSet.contains(t) ? "" : t)
                .collect(Collectors.toList());
        Files.write(gitignoreFile, lines);
    }

    private void updateLogXML() throws IOException
    {
        checkAndCopy(templatePath, projectPath.resolve(PATH_RESOURCES), "log4j2-dev.xml");
        checkAndCopy(templatePath, projectPath.resolve(PATH_RESOURCES), "log4j2-prod.xml");
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
            if (Objects.equals("<properties>", l) || Objects.equals("<dependencies>", l))
            {
                remove = true;
            }
            if (remove)
            {
                removeSet.add(i);
            }
            if (Objects.equals("</properties>", l) || Objects.equals("</dependencies>", l))
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
        Path targetDir = projectPath.resolve(PATH_RESOURCES);
        if (DirUtil.existsDir(targetDir.resolve("META-INF")))
        {
            return;
        }
        DirUtil.copy(templatePath.resolve("META-INF"), targetDir.resolve("META-INF"));
        checkAndCopy(templatePath, targetDir, "application.properties");
        checkAndCopy(templatePath, targetDir, "application-dev.properties");
        checkAndCopy(templatePath, targetDir, "application-prod.properties");
    }

    private void addConfigClass() throws IOException
    {
        Path tempDir = templatePath.resolve("config");
        Path targetDir = projectPath.resolve(PATH_JAVA)
                .resolve(packagePath.replace(".", "/"))
                .resolve("config");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + tempDir);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + targetDir);
        checkAndCopy(tempDir, targetDir, "SwaggerConfig.java");
        checkAndCopy(tempDir, targetDir, "InitConfig.java");
        checkAndCopy(tempDir, targetDir, "WebMvcConfig.java");
    }

    private void updateLogo()
    {
    }

    private void checkAndCopy(Path source, Path target, String fileName) throws IOException
    {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + target);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>" + target.resolve(fileName));
        Optional<File> targetFile = Optional.of(target)
                .map(t -> t.resolve(fileName))
                .filter(p -> FileUtil.existsFile(p))
                .map(p -> p.toFile())
                .filter(f -> f.length() > 10);
        if (targetFile.isPresent())
        {
            return;
        }
        copyAndChange(source, target, fileName);
    }

    private void copyAndChange(Path source, Path target, String fileName) throws IOException
    {
        List<String> lines = Files.lines(source.resolve(fileName)).map(l ->
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
        }).collect(Collectors.toList());
        DirUtil.createDirIfNotExists(target);
        Files.write(target.resolve(fileName), lines);
    }
}
