package person.liuxx.tools.project.java;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 刘湘湘
 * @since 2019年4月11日 下午2:50:57
 */
public class PomList extends ArrayList<String>
{
    private int parentVersionLineIndex = -1;
    private int startRemoveLineIndex = -1;
    private int endRemoveLineIndex = -1;
    private int addIndex = -1;
    private List<String> dependencieList = new ArrayList<>();
    /**
     * 
     */
    private static final long serialVersionUID = 4287999987986654493L;

    public void readPom(Path pomPath, Path templatePath)
    {
        try
        {
            dependencieList = Files.readAllLines(templatePath);
            addAll(Files.readAllLines(pomPath));
            for (int i = 0, max = size(); i < max; i++)
            {
                String l = get(i).trim();
                if (Objects.equals("<artifactId>spring-boot-starter-parent</artifactId>", l) && (i
                        + 1) < max)
                {
                    parentVersionLineIndex = i + 1;
                }
                if (Objects.equals("</parent>", l))
                {
                    addIndex = i + 1;
                }
                if (Objects.equals("<properties>", l))
                {
                    startRemoveLineIndex = i;
                }
                if (Objects.equals("</dependencies>", l))
                {
                    endRemoveLineIndex = i;
                    break;
                }
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void updatePom()
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
        if (addIndex > 1)
        {
            addAll(addIndex, dependencieList);
        }
    }

    public boolean isUpdate()
    {
        return this.stream().anyMatch(l -> l.contains("<swagger.version>"));
    }
}