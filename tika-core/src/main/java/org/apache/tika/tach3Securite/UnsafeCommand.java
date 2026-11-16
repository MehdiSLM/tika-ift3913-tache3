
package org.apache.tika.tach3Securite;

import java.io.IOException;

// Une entrée de metacharacters du shell peuvent modifier commande exec
public class UnsafeCommand {

    public Process executeCommand(String userInput)
            throws IOException {

        String command = "echo " + userInput;

        return new ProcessBuilder(
                "cmd.exe", "/c", command
        ).start();
    }
}
