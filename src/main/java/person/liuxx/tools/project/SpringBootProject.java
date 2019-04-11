package person.liuxx.tools.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import person.liuxx.tools.project.java.PomList;
import person.liuxx.util.base.StringUtil;
import person.liuxx.util.file.DirUtil;
import person.liuxx.util.file.FileOperateException;
import person.liuxx.util.file.FileUtil;

public class SpringBootProject
{
    private final Path projectPath;
    private final Path templatePath;
    private final String packagePath;
    /**
     * 用来替换占位符的map
     */
    private Map<String, String> replaceMap;
    private static final String PATH_RESOURCES = "src/main/resources/";
    private static final String PATH_JAVA = "src/main/java/";
    private static final String PATH_STATIC = "src/main/resources/static/";
    private static final String[] APPLICATION_CONFIG_FILES =
    { "application.properties", "application-dev.properties", "application-prod.properties" };
    private static final String[] JAVA_CONFIG_FILES =
    { "SwaggerConfig.java", "InitConfig.java", "WebMvcConfig.java" };
    private static final String[] LOG_CONFIG_FILES =
    { "log4j2-dev.xml", "log4j2-prod.xml" };

    public SpringBootProject(Path projectPath, String packagePath, String schenmaName,
            Path templatePath)
    {
        this.projectPath = projectPath;
        this.templatePath = templatePath;
        this.packagePath = packagePath;
        replaceMap = new HashMap<>();
        replaceMap.put("projectName", projectPath.getFileName().toString());
        replaceMap.put("schenmaName", schenmaName);
        replaceMap.put("packagePath", packagePath);
    }

    public void update() throws IOException
    {
        addReadmeFile();
        updateGitignore();
        updatePOM();
        updateLogo();
        copyConfigFiles();
    }

    private void addReadmeFile()
    {
        Path readmeFile = projectPath.resolve("README.md");
        if (!FileUtil.existsFile(readmeFile))
        {
            String projectName = projectPath.getFileName().toString();
            List<String> lines = new ArrayList<>();
            lines.add("# " + projectName + "项目说明");
            writeFile(readmeFile, lines);
        }
    }

    private void updateGitignore()
    {
        Path gitignoreFile = projectPath.resolve(".gitignore");
        Set<String> needReplaceBlankSet = Arrays.stream(new String[]
        { ".settings", ".classpath", ".project" }).collect(Collectors.toSet());
        List<String> lines = readFile(gitignoreFile).map(l -> l.trim())
                .map(t -> needReplaceBlankSet.contains(t) ? "" : t)
                .collect(Collectors.toList());
        writeFile(gitignoreFile, lines);
    }

    public void updatePOM() throws IOException
    {
        PomList pomList = new PomList();
        pomList.readPom(projectPath.resolve("pom.xml"), templatePath.resolve("pom.xml"));
        if (!pomList.isUpdate())
        {
            pomList.updatePom();
            writeFile(projectPath.resolve("pom.xml"), pomList);
        }
    }

    private void updateLogo()
    {
    }

    private void copyConfigFiles()
    {
        copyFiles(templatePath, projectPath.resolve(PATH_RESOURCES), LOG_CONFIG_FILES);
        checkAndCopy(templatePath, projectPath.resolve(PATH_STATIC), "index.html");
        Path targetDir = projectPath.resolve(PATH_RESOURCES);
        if (!DirUtil.existsDir(targetDir.resolve("META-INF")))
        {
            DirUtil.copy(templatePath.resolve("META-INF"), targetDir.resolve("META-INF"));
        }
        copyFiles(templatePath, targetDir, APPLICATION_CONFIG_FILES);
        copyConfigJavaFiles();
    }

    private void copyConfigJavaFiles()
    {
        Path targetDir = projectPath.resolve(PATH_JAVA)
                .resolve(packagePath.replace(".", "/"))
                .resolve("config");
        copyFiles(templatePath.resolve("config"), targetDir, JAVA_CONFIG_FILES);
    }

    private void copyFiles(Path source, Path target, String[] fileNames)
    {
        for (String fileName : fileNames)
        {
            checkAndCopy(source, target, fileName);
        }
    }

    /**
     * 将源文件复制到目标位置，文件类型为文本文件<br>
     * 复制过程中如果文件行包含“${}”，那么会根据对象中存储的Map内容将其进行替换（map一般从配置文件中获取）
     * 如果目标文件已经存在，并且内容不为空白（length() > 10），则不进行复制操作
     * 
     * @author 刘湘湘
     * @since 2019年4月10日 下午5:42:40
     * @param source
     *            源文件
     * @param target
     *            目标文件
     * @param fileName
     *            文件名
     * @return
     * @throws IOException
     */
    public boolean checkAndCopy(Path source, Path target, String fileName)
    {
        long notEmptyLineCount = Optional.of(target)
                .map(t -> t.resolve(fileName))
                .filter(p -> FileUtil.existsFile(p))
                .map(p -> readFile(p))
                .orElse(Stream.of(""))
                .filter(s -> !StringUtil.isEmpty(s))
                .count();
        return (notEmptyLineCount > 1) ? false : copyAndChange(source, target, fileName);
    }

    private boolean copyAndChange(Path source, Path target, String fileName)
    {
        List<String> lines = readFile(source.resolve(fileName)).map(l -> replacePlaceholder(l))
                .collect(Collectors.toList());
        DirUtil.createDirIfNotExists(target);
        writeFile(target.resolve(fileName), lines);
        return true;
    }

    private String replacePlaceholder(String text)
    {
        if (!text.contains("${"))
        {
            return text;
        }
        Pattern pattern = Pattern.compile("\\$\\{\\w+?\\}");
        Matcher matcher = pattern.matcher(text);
        Optional<Matcher> optional = Optional.of(matcher);
        return optional.filter(m -> m.find())
                .map(m -> m.group(0))
                .map(m -> m.substring(2, m.length() - 1))
                .filter(k -> replaceMap.containsKey(k))
                .map(key -> text.replace("${" + key + "}", replaceMap.get(key)))
                .orElse(text);
    }

    private Stream<String> readFile(Path p)
    {
        try
        {
            return Files.lines(p);
        } catch (IOException e)
        {
            throw new FileOperateException("获取文件内容失败！", e);
        }
    }

    private void writeFile(Path path, List<String> lines)
    {
        try
        {
            Files.write(path, lines);
        } catch (IOException e)
        {
            throw new FileOperateException("写入文件失败！", e);
        }
    }
}
