package penknife.segment

import penknife.segment.logic.Term
import penknife.segment.logic.WordNet
import penknife.segment.postprocessor.AbstractPostProcessor
import penknife.segment.postprocessor.TimeWordProcessor
import penknife.segment.preprocessor.CleanProcessor
import penknife.segment.preprocessor.T2SProcessor
import penknife.segment.process.DictProcessor
import penknife.segment.processor.PersonNameProcessor
import penknife.segment.processor.TransliterationProcessor

//分词入口
object Segmenter {

    fun split(rawInput: String): List<Term> {

        //预处理
        val preprocessors = listOf(CleanProcessor, T2SProcessor).reduce { a, b -> a andThen b }
        val sentence = preprocessors.process(rawInput)

        val wordNetOrigin = WordNet(sentence)
        //最短路径切分
        val terms = wordNetOrigin.calculateRoute()

        //自定义词典
        val splittedTerms = DictProcessor().process(terms)

        val wordNetOptimized = WordNet(sentence, splittedTerms)

        //更多处理
        val processors = listOf(PersonNameProcessor, TransliterationProcessor).reduce { a, b -> a andThen b }
        processors.process(splittedTerms, wordNetOptimized, wordNetOrigin)

        val processedTerms = wordNetOptimized.calculateRoute()

        val postProcessors = listOf<AbstractPostProcessor>(TimeWordProcessor).reduce { a, b -> a andThen b }
        return postProcessors.process(processedTerms)
    }
}

fun main(args: Array<String>) {
    val s = "龚学平, 万姆尔丘克等领导, 123.45%，优酷总裁魏明介绍了优酷2015年的内容战略"
    val result = Segmenter.split(s)
    println(result.map { it.toString() + "_" })
}