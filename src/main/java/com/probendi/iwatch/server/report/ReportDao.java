package com.probendi.iwatch.server.report;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.probendi.iwatch.server.db.DaoException;
import com.probendi.iwatch.server.db.EntityNotFoundException;
import com.probendi.iwatch.server.user.Watcher;

/**
 * Data Access Object for a {@link Report}.
 *
 * @author Daniele Di Salvo, Copyright (c) 2016-2020 Probendi Limited
 */
public interface ReportDao {

    /**
     * Adds an activity to the given report.
     *
     * @param id       the report's id
     * @param activity the activity to be added
     * @throws DaoException          if the activity could not be added
     * @throws IllegalStateException if the activity cannot be added in the current status
     */
    void addActivity(final @NotNull String id, final @NotNull Activity activity) throws DaoException;

    /**
     * Adds a watcher to the given report.
     *
     * @param id      the report's id
     * @param watcher the watcher to be added
     * @throws DaoException if the watcher cannot be added
     */
    void addWatcher(final @NotNull String id, final @NotNull Watcher watcher) throws DaoException;

    /**
     * Returns the number or reports of the given municipality which must be processed by an administrator.
     *
     * @param municipality the municipality's id
     * @return the number or reports of the given municipality which must be processed by an administrator
     */
    int countReportsToBeProcessed(final @NotNull String municipality);

    /**
     * Deletes the report with the given id.
     *
     * @param id the report's id
     * @throws DaoException          if the report could not be deleted
     * @throws IllegalStateException if the report's status is not {@code CREATA}
     */
    void delete(final @NotNull String id) throws DaoException;

    /**
     * Deletes all reports.
     *
     * @throws DaoException if the reports could not be deleted
     */
    void deleteAll() throws DaoException;

    /**
     * Deletes a watcher from the given report.
     *
     * @param id      the report's id
     * @param watcher the watcher's id
     * @throws DaoException if the watcher cannot be deleted
     */
    void deleteWatcher(final @NotNull String id, final @NotNull String watcher) throws DaoException;

    /**
     * Returns the report with the given id.
     *
     * @param id the report's id
     * @return the report with the given id
     * @throws EntityNotFoundException if no report was found
     */
    Report find(final @NotNull String id) throws EntityNotFoundException;

    /**
     * Returns all reports of the given municipality which have the given watcher among their watchers.
     *
     * @param municipality the municipality's id
     * @param watcher      the watcher's id
     * @param status       the status of the report
     * @return all reports of the given municipality which have the given watcher among their watchers
     */
    List<Report> findAll(final @NotNull String municipality, final @NotNull String watcher, final @NotNull String status);

    /**
     * Returns all reports.
     *
     * @return all reports
     */
    List<Report> findAll();

    /**
     * Inserts the given report.
     *
     * @param report the report to be inserted
     * @throws DaoException if the report could not be inserted
     */
    void insert(final @NotNull Report report) throws DaoException;

    /**
     * Sets the {@code status} field of the report with the given id to 'RIAPERTA' if 'CHIUSA'.
     *
     * @param id the report's id
     * @throws DaoException if the {@code status} field could not be set
     */
    void reopen(final @NotNull String id) throws DaoException;

    /**
     * Sets the {@code actionRequired} field of the report with the given id.
     *
     * @param id    the report's id
     * @param value the new actionRequired
     * @throws DaoException if the {@code actionRequired} field could not be set
     */
    void setActionRequired(final @NotNull String id, final boolean value) throws DaoException;

    /**
     * Updates the given report.
     *
     * @param report the report to be updated
     * @throws DaoException          if the report could not be updated
     * @throws IllegalStateException if next status cannot be reached from the current status
     */
    void update(final @NotNull Report report) throws DaoException;

    /**
     * Validates a state transition.
     *
     * @param from the initial status
     * @param to   the final status
     * @throws IllegalStateException if the {@code to} status cannot be reached from the {@code from} status
     */
    static void validateStateTransition(final @NotNull String from, final @NotNull String to) {
        switch (from) {
            case "CREATA":
                if (!(to.equals("CREATA") || to.equals("APERTA") || to.equals("CHIUSA"))) {
                    throw new IllegalStateException("Invalid state transition from " + from + " to " + to);
                }
                break;
            case "APERTA":
                if (!(to.equals("APERTA") || to.equals("CHIUSA"))) {
                    throw new IllegalStateException("Invalid state transition from " + from + " to " + to);
                }
                break;
            case "CHIUSA":
                if (!(to.equals("CHIUSA") || to.equals("RIAPERTA"))) {
                    throw new IllegalStateException("Invalid state transition from " + from + " to " + to);
                }
                break;
            default:
                if (!(to.equals("RIAPERTA") || to.equals("CHIUSA"))) {
                    throw new IllegalStateException("Invalid state transition from " + from + " to " + to);
                }
        }
    }
}
