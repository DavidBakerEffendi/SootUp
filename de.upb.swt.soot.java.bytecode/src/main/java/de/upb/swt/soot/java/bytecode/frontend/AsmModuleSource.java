package de.upb.swt.soot.java.bytecode.frontend;
/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018-2020 Andreas Dann, Linghui Luo, Christian Brüggemann and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
import de.upb.swt.soot.core.frontend.ResolveException;
import de.upb.swt.soot.java.core.JavaModuleIdentifierFactory;
import de.upb.swt.soot.java.core.JavaModuleInfo;
import de.upb.swt.soot.java.core.ModuleModifier;
import de.upb.swt.soot.java.core.signatures.ModuleSignature;
import de.upb.swt.soot.java.core.types.JavaClassType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.*;

public class AsmModuleSource extends JavaModuleInfo {

  private final ModuleNode module;

  public AsmModuleSource(@Nonnull Path sourcePath) {

    // if it is an automatic module there is no module-info
    super(false);

    try (InputStream sourceFileInputStream = Files.newInputStream(sourcePath)) {
      ClassReader clsr = new ClassReader(sourceFileInputStream);

      ClassNode classNode = new ClassNode(AsmUtil.SUPPORTED_ASM_OPCODE);
      clsr.accept(classNode, ClassReader.SKIP_FRAMES);

      module = classNode.module;

    } catch (IOException e) {
      throw new ResolveException("can not parse module-info!", sourcePath, e);
    }
  }

  @Override
  public ModuleSignature getModuleSignature() {
    return JavaModuleIdentifierFactory.getModuleSignature(module.name);
  }

  @Override
  public Collection<JavaModuleInfo.ModuleReference> requires() {
    ArrayList<JavaModuleInfo.ModuleReference> requieres = new ArrayList<>();

    // add requies
    for (ModuleRequireNode moduleRequireNode : module.requires) {
      JavaClassType classSignature = AsmUtil.asmIDToSignature(moduleRequireNode.module);
      if (classSignature.isModuleInfo()) {
        // sootModuleInfo.addRequire(sootClassOptional.get(), moduleRequireNode.access,
        // moduleRequireNode.version);
        JavaModuleInfo.ModuleReference reference =
            new JavaModuleInfo.ModuleReference(
                classSignature, AsmUtil.getModuleModifiers(moduleRequireNode.access));
        requieres.add(reference);
      }
    }
    return requieres;
  }

  @Override
  public Collection<JavaModuleInfo.PackageReference> exports() {
    ArrayList<JavaModuleInfo.PackageReference> exports = new ArrayList<>();
    for (ModuleExportNode exportNode : module.exports) {
      Iterable<JavaClassType> optionals = AsmUtil.asmIdToSignature(exportNode.modules);
      ArrayList<JavaClassType> modules = new ArrayList<>();
      for (JavaClassType sootClassOptional : optionals) {
        if (sootClassOptional.isModuleInfo()) {
          modules.add(sootClassOptional);
        }
      }
      // FIXME: create constructs here
      // sootModuleInfo.addExport(exportNode.packaze, exportNode.access, modules);
      JavaModuleInfo.PackageReference reference =
          new JavaModuleInfo.PackageReference(
              exportNode.packaze, AsmUtil.getModuleModifiers(exportNode.access), modules);
      exports.add(reference);
    }
    return exports;
  }

  @Override
  public Collection<JavaModuleInfo.PackageReference> opens() {
    ArrayList<JavaModuleInfo.PackageReference> opens = new ArrayList<>();
    /// add opens
    for (ModuleOpenNode moduleOpenNode : module.opens) {
      Iterable<JavaClassType> optionals = AsmUtil.asmIdToSignature(moduleOpenNode.modules);
      ArrayList<JavaClassType> modules = new ArrayList<>();
      for (JavaClassType sootClassOptional : optionals) {
        if (sootClassOptional.isModuleInfo()) {
          modules.add(sootClassOptional);
        }
      }

      JavaModuleInfo.PackageReference reference =
          new JavaModuleInfo.PackageReference(
              moduleOpenNode.packaze, AsmUtil.getModuleModifiers(moduleOpenNode.access), modules);
      opens.add(reference);
    }

    return opens;
  }

  @Override
  public Collection<JavaClassType> provides() {
    ArrayList<JavaClassType> providers = new ArrayList<>();
    // add provides
    for (ModuleProvideNode moduleProvideNode : module.provides) {
      JavaClassType serviceSignature = AsmUtil.asmIDToSignature(moduleProvideNode.service);
      Iterable<JavaClassType> providersSignatures =
          AsmUtil.asmIdToSignature(moduleProvideNode.providers);
      for (JavaClassType sootClassSignature : providersSignatures) {
        providers.add(sootClassSignature);
      }

      providers.add(serviceSignature);
    }

    return providers;
  }

  @Override
  public Collection<JavaClassType> uses() {
    ArrayList<JavaClassType> uses = new ArrayList<>();
    // add uses
    for (String usedService : module.uses) {
      JavaClassType serviceSignature = AsmUtil.asmIDToSignature(usedService);
      uses.add(serviceSignature);
    }

    return uses;
  }

  @Override
  public Set<ModuleModifier> resolveModifiers() {
    return AsmUtil.getModuleModifiers(module.access);
  }
}
