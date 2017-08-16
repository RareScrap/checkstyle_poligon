package com.lsurvila.checkstyle;
import com.puppycrawl.tools.checkstyle.api.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.xml.ws.LogicalMessage;

import static java.awt.SystemColor.text;

public class LinesAfterClassJavadocCheck extends AbstractCheck
{
    private static final int DEFAULT_MAX = 30;
    private int max = DEFAULT_MAX;

    /**
     * Returns the default token a check is interested in. Only used if the
     * configuration for a check does not define the tokens.
     *
     * @return the default tokens
     * @see TokenTypes
     */
    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF};
    }

    /**
     * The configurable token set.
     * Used to protect Checks against malicious users who specify an
     * unacceptable token set in the configuration file.
     * The default implementation returns the check's default tokens.
     *
     * @return the token set this check is designed for.
     * @see TokenTypes
     */
    @Override
    public int[] getAcceptableTokens() {
        return new int[]{TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF};
    }

    /**
     * The tokens that this check must be registered for.
     *
     * @return the token set this must be registered for.
     * @see TokenTypes
     */
    @Override
    public int[] getRequiredTokens() {
        return new int[]{TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF};
    }

    @Override
    public void visitToken(DetailAST ast)
    {
        /* Первое! Перед вам бессмысленное выражение - поиск CLASS_DEF внутри CLASS_DEF.
        Результат, естественно, null...
         */
        DetailAST objBlock1 = ast.findFirstToken(TokenTypes.CLASS_DEF);

        // Дана строка вызовет NPE ...
        //String out = objBlock1.getText();
        /* ... но в консоле будет такое сообщение:
        :app:checkstyle FAILED

        FAILURE: Build failed with an exception.

        * What went wrong:
        Execution failed for task ':app:checkstyle'.
        > Unable to process files: [D:\Users\rares\Downloads\checkstyle_poligon\app\src\main\java\com\lsurvila\checkstylebuilder\DataProvider.java, D:\Users\rares\Downloads\checksty
        le_poligon\app\src\main\java\com\lsurvila\checkstylebuilder\MainActivity.java, D:\Users\rares\Downloads\checkstyle_poligon\app\src\main\java\com\lsurvila\checkstylebuilder\P
        arcelableExample.java]

        * Try:
        Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output.

        BUILD FAILED

        Сообщение вида Unabe to process files нам показалось очень неинформативным. Было бы здорово, если это сообщение сразу обьясняло почему случился краш.
        Если запустить задачу Checkstyle с параметром stacktrace (gradlew --stacktrace checkstyle), то можно установить что краш сслучился из-за того, что objBlock1 == null
         */


        /* По поводу того, что Log() выдает краш - это наша вина. Мы просто делали getLineNo на null-объекте */
        // log успешно работает
        //log(ast.getLineNo(), "It's log!");

        /* Второе! Задача простая - вывести в лог номер строки и содержание этой строки

        Это сработает на 23 строке при анализе DataProvider.
        Но почему в консоль выведется содержимое 24-ой строки?
        Чтобы решить эту задачу, придется использовать такую конструкцию - ast.getLineNo()-1.
        А это не очеь удобно и смотрится некрасиво

         */
        log(ast.getLineNo(), getLine(ast.getLineNo()));

    }
}