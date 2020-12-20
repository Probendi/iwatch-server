package com.probendi.iwatch.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Helper class for generating {@code CSV} reports.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public class CsvUtils {

    /**
     * Converts the given {@code field} into a comma-terminated {@code CSV}-compliant string.
     *
     * @param field the field to be converted into a comma-terminated {@code CSV}-compliant string
     * @return a {@code CSV} compliant string
     */
    @Contract(pure = true)
    @NotNull
    public static String toCSV(final boolean field) {
        return field + ",";
    }

    /**
     * Converts the given {@code field} into a comma-terminated {@code CSV}-compliant string.
     *
     * @param field the date to be converted into a comma-terminated {@code CSV}-compliant string
     * @param datetime {@code true} if it is a date-time field or {@code false} if it is a date only field
     * @return a {@code CSV} compliant string
     */
    @NotNull
    public static String toCSV(final Date field, final boolean datetime) {
        final PropertiesReader reader = new PropertiesReader();
        final SimpleDateFormat sdf = datetime ? reader.getDateTimeFormat() : reader.getDateFormat();
        return field == null ? "," : sdf.format(field) + ",";
    }

    /**
     * Converts the given {@code field} into a comma-terminated {@code CSV}-compliant string.
     *
     * @param field the field to be converted into a comma-terminated {@code CSV}-compliant string
     * @return a {@code CSV} compliant string
     */
    @Contract(pure = true)
    @NotNull
    public static String toCSV(final double field) {
        return field + ",";
    }

    /**
     * Converts the given {@code field} into a comma-terminated {@code CSV}-compliant string.
     *
     * @param field the field to be converted into a comma-terminated {@code CSV}-compliant string
     * @return a {@code CSV} compliant string
     */
    @Contract(pure = true)
    @NotNull
    public static String toCSV(final int field) {
        return field + ",";
    }

    /**
     * Converts the given {@code field} into a comma-terminated {@code CSV}-compliant field. If a comma occurs within
     * {@code field}, then {@code field} will be surrounded by double quotes. If a double quote occurs within
     * {@code field}, then {@code field} will be surrounded by double quotes and the double quote within the field
     * will be escaped by another double quote.
     *
     * @param field the field to be converted into a comma-terminated {@code CSV}-compliant field
     * @return a {@code CSV} compliant field
     */
    @NotNull
    public static String toCSV(String field) {
        if (field == null) {
            return ",";
        }
        field = field.replace("\"", "\"\"");
        return ((field.contains(",") || field.contains("\"")) ? "\"" + field + "\"" : field) + ",";
    }

    /**
     * Converts the given {@code field} into a comma-terminated {@code CSV}-compliant string.
     *
     * @param field the list to be converted into a comma-terminated {@code CSV}-compliant string
     * @return a {@code CSV} compliant string
     */
    @NotNull
    public static String toCSV(final @NotNull List<String> field) {
        final List<String> list = field.stream()
                .map(string -> "\"" + string + "\"").collect(Collectors.toCollection(LinkedList::new));
        return toCSV(list.toString());
    }
}
