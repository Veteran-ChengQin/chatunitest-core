package zju.cst.aces.api.impl;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import lombok.Data;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import zju.cst.aces.api.Validator;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.dto.PromptInfo;
import zju.cst.aces.util.TestCompiler;

import java.nio.file.Path;
import java.util.List;

import zju.cst.aces.api.Validator;

/**
 * Validate the test code by compiling and running the unit.
 */
@Data
public class ValidatorImpl implements Validator {

    TestCompiler compiler;

    public ValidatorImpl(Path testOutputPath, Path compileOutputPath, Path targetPath, List<String> classpathElements) {
        this.compiler = new TestCompiler(testOutputPath, compileOutputPath, targetPath, classpathElements);
    }

    @Override
    public boolean syntacticValidate(String code) {
        try {
            StaticJavaParser.parse(code);
            return true;
        } catch (ParseProblemException e) {
            return false;
        }
    }

    /**
     * Compile test file.
     */
    @Override
    public boolean semanticValidate(String code, String className, Path outputPath, PromptInfo promptInfo) {
        compiler.setCode(code);
        return compiler.compileTest(className, outputPath, promptInfo);
    }
    /**
     * Execute the test file to verify the file.
     */
    @Override
    public boolean runtimeValidate(String fullTestName) {
        return compiler.executeTest(fullTestName).getTestsFailedCount() == 0;
    }
    /**
     * Compile test file.
     */
    @Override
    public boolean compile(String className, Path outputPath, PromptInfo promptInfo) {
        return compiler.compileTest(className, outputPath, promptInfo);
    }
    /**
     * Execute test file.
     */
    @Override
    public TestExecutionSummary execute(String fullTestName) {
        return compiler.executeTest(fullTestName);
    }
}
