package reviewssearch.translation

case class TranslationRequest(input_lang: String, output_lang: String, text: String)
case class TranslationRespond(text: String)
