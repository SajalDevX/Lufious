package ai.lufious.app.presentation.garden.viewmodel

sealed class PlantDetailEvent {
    data class LogTypeSelected(val type: String) : PlantDetailEvent()
    data class NoteChanged(val note: String) : PlantDetailEvent()
    object ShowLogDialog : PlantDetailEvent()
    object DismissLogDialog : PlantDetailEvent()
    object SubmitLog : PlantDetailEvent()
}
