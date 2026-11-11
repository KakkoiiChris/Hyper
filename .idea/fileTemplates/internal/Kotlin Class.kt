#parse("HyperFileHeader.kt")
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")
package ${PACKAGE_NAME}
#end

#parse("HyperClassHeader.kt")
class ${NAME} {
}