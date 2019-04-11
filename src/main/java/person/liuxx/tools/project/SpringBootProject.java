package person.liuxx.tools.project;

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
import java.util.stream.Stream;

import person.liuxx.tools.exception.ReadFileException;
import person.liuxx.util.base.StringUtil;
import person.liuxx.util.file.DirUtil;
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

    class PomList extends ArrayList<String>
    {
        int parentVersionLineIndex = -1;
        int startRemoveLineIndex = -1;
        int endRemoveLineIndex = -1;
        List<String> dependencieList = new ArrayList<>();
        /**
         * 
         */
        private static final long serialVersionUID = 4287999987986654493L;

        void readPom(Path pomPath, Path templatePath)
        {
            try
            {
                dependencieList = Files.readAllLines(templatePath);
                addAll(Files.readAllLines(pomPath));
                for (int i = 0, max = size(); i < max; i++)
                {
                    String l = get(i).trim();
                    if (Objects.equals("<artifactId>spring-boot-starter-parent</artifactId>", l)
                            && (i + 1) < max)
                    {
                        parentVersionLineIndex = i + 1;
                    }
                    if (Objects.equals("<properties>", l))
                    {
                        startRemoveLineIndex = i;
                    }
                    if (Objects.equals("</dependencies>", l))
                    {
                        endRemoveLineIndex = i;
                    }
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        void updatePom()
        {
            if (parentVersionLineIndex > 0)
            {
                set(parentVersionLineIndex, "\t\t<version>2.0.3.RELEASE</version>");
            }
            if (startRemoveLineIndex > 0 && endRemoveLineIndex > startRemoveLineIndex
                    && endRemoveLineIndex < size())
            {
                removeRange(startRemoveLineIndex, endRemoveLineIndex + 1);
            }
            for (int i = 0, max = size(); i < max; i++)
            {
                String l = get(i).trim();
                if (Objects.equals("</parent>", l))
                {
                    addAll(i + 1, dependencieList);
                }
            }
        }

        boolean isUpdate()
        {
            return this.stream().anyMatch(l -> l.contains("<swagger.version>"));
        }
    }

    public void updatePOM() throws IOException
    {
        PomList pomList = new PomList();
        pomList.readPom(projectPath.resolve("pom.xml"), templatePath.resolve("pom.xml"));
        if (pomList.isUpdate())
        {
            return;
        } else
        {
            pomList.updatePom();
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
        checkAndCopy(tempDir, targetDir, "SwaggerConfig.java");
        checkAndCopy(tempDir, targetDir, "InitConfig.java");
        checkAndCopy(tempDir, targetDir, "WebMvcConfig.java");
    }

    private void updateLogo()
    {
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
    public boolean checkAndCopy(Path source, Path target, String fileName) throws IOException
    {
        long notEmptyLineCount = Optional.of(target)
                .map(t -> t.resolve(fileName))
                .filter(p -> FileUtil.existsFile(p))
                .map(p -> getStream(p))
                .orElse(Stream.of(""))
                .filter(s -> !StringUtil.isEmpty(s))
                .count();
        if (notEmptyLineCount > 1)
        {
            return false;
        }
        copyAndChange(source, target, fileName);
        return true;
    }

    private Stream<String> getStream(Path p)
    {
        try
        {
            return Files.lines(p);
        } catch (IOException e)
        {
            throw new ReadFileException("获取文件内容失败！", e);
        }
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
                    if (replaceMap.containsKey(key))
                    {
                        return l.replace("${" + key + "}", replaceMap.get(key));
                    }
                }
            }
            return l;
        }).collect(Collectors.toList());
        DirUtil.createDirIfNotExists(target);
        Files.write(target.resolve(fileName), lines);
    }
}
