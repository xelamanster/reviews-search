package reviewssearch.translation

sealed trait Lang
case object En extends Lang
case object Fr extends Lang

case class TranslationRequest(input_lang: Lang, output_lang: Lang, text: String)
case class TranslationRespond(text: String)
