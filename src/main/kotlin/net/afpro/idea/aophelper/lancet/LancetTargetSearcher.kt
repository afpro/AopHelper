package net.afpro.idea.aophelper.lancet

import com.intellij.psi.PsiMethod
import com.intellij.util.Processor
import com.intellij.util.QueryExecutor
import com.intellij.util.QueryFactory

internal object LancetTargetSearcher : QueryFactory<List<PsiMethod>, LancetAopType>(), QueryExecutor<List<PsiMethod>, LancetAopType> {
    init {
        registerExecutor(this)
    }

    override fun execute(aopType: LancetAopType, consumer: Processor<in List<PsiMethod>>): Boolean {
        TODO("finish this")
    }
}