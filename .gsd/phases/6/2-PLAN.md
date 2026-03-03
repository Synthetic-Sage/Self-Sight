---
phase: 6
plan: 2
wave: 1
---

# Plan 6.2: Data Export (CSV via Android SAF)

## Objective
Implement journal data export to CSV using the Android Storage Access Framework (SAF). The user taps "Export Journal Data" in Settings, a system file picker opens, they choose a save location, and the app writes a CSV with all journal entries.

## Context
- app/src/main/java/com/diary/mirroroftruth/domain/repository/JournalEntryRepository.kt
- app/src/main/java/com/diary/mirroroftruth/domain/model/JournalEntry.kt
- No new permissions required — SAF handles storage access.

## Tasks

<task type="auto">
  <name>Create ExportViewModel</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/settings/SettingsViewModel.kt
  </files>
  <action>
    - Create SettingsViewModel @Inject constructor(private val journalRepo: JournalEntryRepository, @ApplicationContext private val context: Context) : ViewModel().
    - State:
        data class SettingsState(val exportStatus: String = "", val showClearConfirmDialog: Boolean = false)
    - Event:
        sealed interface SettingsEvent { data class OnExportToUri(val uri: Uri) : SettingsEvent; object OnClearDataRequested : SettingsEvent; object OnClearDataConfirmed : SettingsEvent; object OnDismissDialog : SettingsEvent }
    - In onEvent:
        OnExportToUri(uri): Collect all entries via journalRepo.getJournalEntriesBetween(0L, Long.MAX_VALUE).first(), then write CSV:
            CSV header: "date,mood,wentWell,challenges,gratitude,tomorrowsTask"
            For each entry, parse promptResponses.split("|") to get wentWell/challenges/gratitude
            Write using context.contentResolver.openOutputStream(uri) with BufferedWriter.
            Update exportStatus to "Exported {n} entries!" on success or "Export failed" on error.
        OnClearDataRequested: set showClearConfirmDialog = true.
        OnClearDataConfirmed: no-op for now (placeholder — risky operation, let user decide in a later iteration).
        OnDismissDialog: set showClearConfirmDialog = false.
    - Add import for kotlinx.coroutines.flow.first.
  </action>
  <verify>Get-ChildItem -Path app/src/main/java/com/diary/mirroroftruth/presentation/settings/SettingsViewModel.kt</verify>
  <done>SettingsViewModel.kt exists with export-to-URI logic and dialog state.</done>
</task>

<task type="auto">
  <name>Wire SAF Launcher in SettingsScreen + SettingsActivity integration</name>
  <files>
    - app/src/main/java/com/diary/mirroroftruth/presentation/settings/SettingsScreen.kt
  </files>
  <action>
    - Update SettingsScreen signature to: fun SettingsScreen(state: SettingsState, onEvent: (SettingsEvent) -> Unit).
    - Inside the composable, add a rememberLauncherForActivityResult for ActivityResultContracts.CreateDocument("text/csv").
        On result URI received, call onEvent(SettingsEvent.OnExportToUri(uri)).
    - The export row's onClick triggers launcher.launch("mirror_journal_export.csv").
    - Show state.exportStatus as a small Text below the export row if non-empty.
    - Show an AlertDialog when state.showClearConfirmDialog = true:
        Title: "Clear All Data?"
        Text: "This action cannot be undone."
        Confirm button: onEvent(SettingsEvent.OnClearDataConfirmed)
        Dismiss button: onEvent(SettingsEvent.OnDismissDialog)
  </action>
  <verify>Get-Content app/src/main/java/com/diary/mirroroftruth/presentation/settings/SettingsScreen.kt | Select-String "rememberLauncherForActivityResult"</verify>
  <done>SettingsScreen uses SAF launcher and is connected to SettingsViewModel events.</done>
</task>

## Success Criteria
- [ ] SettingsViewModel creates a valid CSV and writes it to the SAF URI.
- [ ] SettingsScreen launches the system file picker on Export tap.
- [ ] Clear Data dialog is shown/dismissed correctly.
