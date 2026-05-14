#!/bin/bash
# Run_Tests.sh
# QA y Quality Gate en Bash (v3.1)

source "$(dirname "$0")/../00_Core_Loader.sh"

write_wf_log "--- INICIANDO QA & QUALITY GATE (BASH) ---" "Cyan"

if [ ! -f "pom.xml" ]; then
    write_wf_log "ERROR: No se encontró pom.xml." "Red"
    exit 1
fi

write_wf_log "Ejecutando Maven Test + JaCoCo..." "Yellow"
mvn clean test jacoco:report

if [ $? -ne 0 ]; then
    write_wf_log "ERROR: Las pruebas han fallado." "Red"
    exit 1
fi

# 2. Validar Quality Gate (Cobertura)
JACOCO_XML="target/site/jacoco/jacoco.xml"
if [ -f "$JACOCO_XML" ]; then
    # Extracción mejorada: buscamos el primer <counter type="INSTRUCTION"> que es el resumen global
    LINE_STATS=$(grep '<counter type="INSTRUCTION"' "$JACOCO_XML" | head -n 1)
    
    MISSED=$(echo "$LINE_STATS" | sed -n 's/.*missed="\([0-9]*\)".*/\1/p')
    COVERED=$(echo "$LINE_STATS" | sed -n 's/.*covered="\([0-9]*\)".*/\1/p')
    
    TOTAL=$((MISSED + COVERED))
    COVERAGE_PERCENT=$(( 100 * COVERED / TOTAL ))
    MIN_REQUIRED=$(jq -r '.quality_gate.min_coverage' "$CONFIG_PATH")

    write_wf_log "RESULTADO COBERTURA: $COVERAGE_PERCENT%" "Cyan"

    if [ "$COVERAGE_PERCENT" -lt "$MIN_REQUIRED" ]; then
        write_wf_log "--- QUALITY GATE FALLIDO ($COVERAGE_PERCENT% < $MIN_REQUIRED%) ---" "Red"
        exit 1
    else
        write_wf_log "--- QUALITY GATE SUPERADO ---" "Green"
    fi
else
    write_wf_log "AVISO: No se encontró jacoco.xml." "Yellow"
fi
