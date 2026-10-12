# Copyright (c) 2026 curforever. All Rights Reserved.
# Dual-licensed under LGPL 2.1 or later and Apache License 2.0.
# See LGPL2.1 and AL2.0 distributed with JNA.
# Android selects the legacy options cache and never loads the JVM-only backend.
# Older Android boot class paths do not declare this optional Java API.
-dontwarn java.lang.ClassValue
