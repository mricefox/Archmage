package com.mricefox.archmage.build.gradle.module.properties;

import com.android.utils.FileUtils;
import com.google.gson.Gson;
import com.mricefox.archmage.build.gradle.extension.ArchmageExtension;
import com.mricefox.archmage.build.gradle.module.properties.ArchmageModuleProperties;

import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import groovy.util.Node;
import groovy.util.XmlParser;

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2017/12/14
 */

public class ModulePropertiesProcessor {
    private final Project project;
    private final Gson gson = new Gson();

    public ModulePropertiesProcessor(Project project) {
        this.project = project;
    }

    public File generateProperties(String activatorClassName, File targetDirectory) throws IOException {
        String packageName = getProjectPackageName(project);
        String jsonTxt = gson.toJson(new ArchmageModuleProperties(packageName, activatorClassName));

        File jsonFile = new File(targetDirectory, "ArchmageModuleProperties.json");

        project.delete(jsonFile);
        FileUtils.createFile(jsonFile, jsonTxt);

        return jsonFile;
    }

    public ArchmageModuleProperties findArchmageModuleProperties(File aar) {
        ArchmageModuleProperties pluginProperties = null;

        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(aar))) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                if (!entry.isDirectory() &&
                        "archmage/ArchmageModuleProperties.json".equals(entry.getName().replaceAll("\\\\", "/"))) {
                    InputStreamReader isr = new InputStreamReader(zipIn);
                    pluginProperties = gson.fromJson(isr, ArchmageModuleProperties.class);
                    zipIn.closeEntry();
                    break;
                }
                zipIn.closeEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pluginProperties;
    }

    public static String getProjectPackageName(Project project) {
        try {
            Node manifest = new XmlParser().parse(project.file("src/main/AndroidManifest.xml"));
            return (String) manifest.attribute("package");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
