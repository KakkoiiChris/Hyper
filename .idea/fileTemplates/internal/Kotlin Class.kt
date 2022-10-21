#parse("HyperHeader.kt")
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")
package ${PACKAGE_NAME}
#end

#parse("FileHeader.kt")
class ${NAME} {
}