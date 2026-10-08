from __future__ import annotations

import re
import sys
import xml.etree.ElementTree as ET
from collections import defaultdict
from pathlib import Path

PLACEHOLDER_RE = re.compile(r"%(?:\d+\$)?[-#+ 0,(]*\d*(?:\.\d+)?[a-zA-Z]")
CODE_REF_RE = re.compile(r"\bRes\.string\.([A-Za-z0-9_]+)")
SKIP_DIRS = {"build", ".gradle", ".git", ".idea", "node_modules"}


def iter_files(root: Path, pattern: str):
    for path in root.rglob(pattern):
        if not SKIP_DIRS.intersection(path.relative_to(root).parts):
            yield path


def parse_strings(path: Path, errors: list[str]) -> dict[str, str]:
    result: dict[str, str] = {}
    try:
        tree = ET.parse(path)
    except ET.ParseError as e:
        errors.append(f"{path}: невалидный XML: {e}")
        return result

    for el in tree.getroot().findall("string"):
        name = el.get("name")
        if not name:
            errors.append(f"{path}: <string> без атрибута name")
            continue
        if el.get("translatable") == "false":
            continue
        if name in result:
            errors.append(f"{path}: дубликат ключа '{name}'")
        value = "".join(el.itertext()).strip()
        if not value:
            errors.append(f"{path}: пустое значение у '{name}'")
        result[name] = value
    return result


def placeholders(value: str) -> list[str]:
    return sorted(PLACEHOLDER_RE.findall(value.replace("%%", "")))


def main() -> int:
    root = Path(sys.argv[1] if len(sys.argv) > 1 else ".").resolve()
    errors: list[str] = []

    modules: dict[Path, dict[str, Path]] = defaultdict(dict)
    for path in iter_files(root, "strings.xml"):
        if path.parent.name == "values" or path.parent.name.startswith("values-"):
            modules[path.parent.parent][path.parent.name] = path

    if not modules:
        print(f"strings.xml не найдены в {root}", file=sys.stderr)
        return 1

    base_keys: set[str] = set()
    for module, variants in sorted(modules.items()):
        rel = module.relative_to(root) if module != root else Path(".")
        if "values" not in variants:
            errors.append(f"{rel}: нет базовой локали values/strings.xml")
            continue

        base = parse_strings(variants["values"], errors)
        base_keys |= set(base)

        for variant, path in sorted(variants.items()):
            if variant == "values":
                continue
            locale = variant.removeprefix("values-")
            tr = parse_strings(path, errors)

            for key in sorted(base.keys() - tr.keys()):
                errors.append(f"{rel} [{locale}]: нет перевода для '{key}'")
            for key in sorted(tr.keys() - base.keys()):
                errors.append(f"{rel} [{locale}]: лишний ключ '{key}' (нет в базовой локали)")
            for key in sorted(base.keys() & tr.keys()):
                if placeholders(base[key]) != placeholders(tr[key]):
                    errors.append(
                        f"{rel} [{locale}]: плейсхолдеры '{key}' не совпадают: "
                        f"{placeholders(base[key])} vs {placeholders(tr[key])}"
                    )

        print(f"{rel}: локали {', '.join(sorted(variants))}, ключей в базе: {len(base)}")

    # Ссылки из кода на несуществующие ключи
    for path in iter_files(root, "*.kt"):
        text = path.read_text(encoding="utf-8", errors="ignore")
        for key in sorted(set(CODE_REF_RE.findall(text))):
            if key not in base_keys:
                errors.append(f"{path.relative_to(root)}: Res.string.{key} не определён в strings.xml")

    if errors:
        print(f"\nНайдено проблем: {len(errors)}", file=sys.stderr)
        for e in errors:
            print(f"  - {e}", file=sys.stderr)
        return 1

    print("OK: локали согласованы")
    return 0


if __name__ == "__main__":
    sys.exit(main())