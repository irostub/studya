package com.irostub.studya;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

public class PackageDependencyTest {

    private static final String STUDY = "..modules.study..";
    private static final String EVENT = "..modules.event..";
    private static final String ACCOUNT = "..modules.account..";
    private static final String TAG = "..modules.tag..";
    private static final String ZONE = "..modules.zone..";
    private static final String MODULES = "com.irostub.studya.modules..";

    JavaClasses importedClasses = new ClassFileImporter().importPackages("com.irostub.studya");

    //모듈 패키지는 모듈 패키지만 참조한다.
    @Test
    public void modulesModuleTest() {
        ArchRule rule = classes().that().resideInAnyPackage(MODULES)
                .should().onlyBeAccessed().byClassesThat()
                .resideInAnyPackage(MODULES);
        rule.check(importedClasses);
    }

    //study 패키지는 스터디와 이벤트가 참조한다.
    /*@Test
    public void studyModuleTest() {
        ArchRule rule = classes().that().resideInAnyPackage(STUDY)
                .should().onlyBeAccessed().byClassesThat()
                .resideInAnyPackage(STUDY, EVENT);
        rule.check(importedClasses);
    }*/

    //event 패키지는 이벤트, 스터디, 어카운트를 참조한다.
    @Test
    public void eventModuleTest() {
        ArchRule rule = classes().that().resideInAnyPackage(EVENT)
                .should().accessClassesThat().resideInAnyPackage(EVENT, STUDY, ACCOUNT);
        rule.check(importedClasses);
    }

    //account 패키지는 어카운트와 존과 태그를 참조한다.
    @Test
    public void accountModuleTest() {
        ArchRule rule = classes().that().resideInAnyPackage(ACCOUNT)
                .should().accessClassesThat().resideInAnyPackage(ACCOUNT, ZONE, TAG);
        rule.check(importedClasses);
    }

    //순환 참조가 발생하는지 확인.
    @Test
    public void cycleModuleTest() {
        slices().matching("com.irostub.studya.modules.(*)..")
                .should().beFreeOfCycles();
    }
}