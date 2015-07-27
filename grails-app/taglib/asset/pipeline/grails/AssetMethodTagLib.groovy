package asset.pipeline.grails


import static org.apache.commons.lang.StringUtils.trimToEmpty


class AssetMethodTagLib {

	static namespace = 'g'
	static returnObjectForTags = ['assetPath']


	def assetProcessorService
	def grailsApplication
	def grailsLinkGenerator


	def assetPath = {final Map<String, ?> attrs ->
		final def src
		final boolean absolute
		if (attrs instanceof Map) {
			src = attrs.src
			absolute = attrs.containsKey('absolute') ? attrs.absolute : false
		}
		else {
			src = attrs
			absolute = false
		}

		final def conf = grailsApplication.config.grails.assets

		final String assetUrl = assetUriRootPath(absolute)

		if (conf.precompiled && src) {
			final def realPath = conf.manifest.getProperty(src)
			if (realPath) {
				return "${assetUrl}${realPath}"
			}
		}
		return "${assetUrl}${src}"
	}

	private String assetUriRootPath(final boolean absolute) {
		final String mapping = assetProcessorService.assetMapping

		def configUrl = grailsApplication.config.grails.assets.url

		if (configUrl instanceof Closure) {
			configUrl = configUrl.call(request)
		}

		if (configUrl) {
			return configUrl
		}
		else if (absolute) {
			return [grailsLinkGenerator.serverBaseURL, "$mapping/"].join('/')
		}

		final String contextPath = trimToEmpty(grailsLinkGenerator.contextPath)

		return contextPath + "${contextPath.endsWith('/') ? '' : '/'}$mapping/"
	}
}
