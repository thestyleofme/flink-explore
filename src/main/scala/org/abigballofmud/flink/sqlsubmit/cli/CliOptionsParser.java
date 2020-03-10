package org.abigballofmud.flink.sqlsubmit.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
/**
 * <p>
 * description
 * </p>
 *
 * @author isacc 2020/03/10 16:52
 * @since 1.0
 */
public class CliOptionsParser {

    private CliOptionsParser(){
        // private
    }

    public static final Option OPTION_WORKING_SPACE = Option
            .builder("w")
            .required(true)
            .longOpt("working_space")
            .numberOfArgs(1)
            .argName("working space dir")
            .desc("The working space dir.")
            .build();

    public static final Option OPTION_SQL_FILE = Option
            .builder("f")
            .required(true)
            .longOpt("file")
            .numberOfArgs(1)
            .argName("SQL file path")
            .desc("The SQL file path.")
            .build();

    public static final Options CLIENT_OPTIONS = getClientOptions(new Options());

    public static Options getClientOptions(Options options) {
        options.addOption(OPTION_SQL_FILE);
        options.addOption(OPTION_WORKING_SPACE);
        return options;
    }

    // --------------------------------------------------------------------------------------------
    //  Line Parsing
    // --------------------------------------------------------------------------------------------

    public static CliOptions parseClient(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("./sql-submit -w <work_space_dir> -f <sql-file>");
        }
        try {
            DefaultParser parser = new DefaultParser();
            CommandLine line = parser.parse(CLIENT_OPTIONS, args, true);
            return new CliOptions(
                    line.getOptionValue(CliOptionsParser.OPTION_SQL_FILE.getOpt()),
                    line.getOptionValue(CliOptionsParser.OPTION_WORKING_SPACE.getOpt())
            );
        }
        catch (ParseException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
