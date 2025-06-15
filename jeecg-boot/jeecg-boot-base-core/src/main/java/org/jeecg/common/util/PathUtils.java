package org.jeecg.common.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class PathUtils {

    /**
     * 拼接文件目录路径
     *
     * @param parts 路径片段数组
     * @return 拼接后的标准路径
     */
    public static String combinePaths(String... parts) {
        if (parts == null || parts.length == 0) {
            return "";
        }

        // 去除每个路径片段首尾的斜杠，并转换为标准化格式
        String[] trimmedParts = Arrays.stream(parts)
                .filter(Objects::nonNull)
                .map(part -> part.trim()
                        .replaceAll("^[/\\\\]+", "")   // 去除开头的 / 或 \
                        .replaceAll("[/\\\\]+$", "")) // 去除结尾的 / 或 \
                .toArray(String[]::new);

        // 使用 Paths 拼接路径
        Path path = Paths.get("", trimmedParts);
        return path.toString();
    }

    /*public static void main(String[] args) {
        // 示例调用
        System.out.println(combinePaths("/home/", "/user/data")); // 输出: homeser\data 或 home/user/data（取决于操作系统）
        System.out.println(combinePaths("C:\\temp\\", "\\files")); // 输出: C:\temp\files
        System.out.println(combinePaths("a", "b/c", "/d/"));       // 输出: a\b\c\d
    }*/
}
