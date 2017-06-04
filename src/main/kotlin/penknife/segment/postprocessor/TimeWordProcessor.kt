package penknife.segment.postprocessor

import penknife.segment.logic.Term

object TimeWordProcessor : AbstractPostProcessor() {

    val processor = penknife.segment.process.TimeWordProcessor()

    override fun process(terms: List<Term>): List<Term> {
        return processor.process(terms)
    }

}