package de.moyapro.nushppinglist.ui.model

import androidx.lifecycle.ViewModel
import de.moyapro.nushppinglist.db.dao.RecipeDao
import de.moyapro.nushppinglist.mock.RecipeDaoMock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob

@FlowPreview
class RecipeViewModel(
    private val cartDao: RecipeDao
) : ViewModel() {

    @Suppress("unused") // no-args constructor required by 'by viewmodels()'
    constructor() : this(RecipeDaoMock(CoroutineScope(Dispatchers.IO + SupervisorJob())))
}

