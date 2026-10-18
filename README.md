# Aplicación Vuelos · CAPEBE (DSY1105, Evaluación Parcial 2)

App Android (Kotlin, Jetpack Compose, Material 3) para el rol **socio** del Club Aéreo del Personal de BancoEstado.

**Integrantes:** (completar con nombres y número de equipo)

## Funcionalidades
- Registro e inicio de sesión con formularios validados (ícono y mensaje por campo).
- Persistencia local con Room (SQLite): los usuarios quedan guardados y el login los consulta. La contraseña se guarda con hash SHA-256.
- Menú del socio con dos funcionalidades: reservar un vuelo e ir al aeródromo (pase de abordaje, habilitado solo si hay un vuelo aceptado).
- Vuelos disponibles en tarjetas (Card), con filtro por fecha usando el **calendario nativo** del dispositivo.
- Detalle del vuelo con opción **Aceptar este vuelo**, que abre la **app de correo nativa** con la confirmación redactada.
- Consentimiento de vuelo en tarjeta, con estado PENDIENTE/FIRMADO que cambia de color con animación.
- Pase de abordaje con los datos del socio y del vuelo.
- Animaciones: entrada del logo, mensajes de error, botón de registro, tarjeta expandible y estado del consentimiento.
- Datos de vuelos ficticios (Ley 19.628).

## Recursos nativos
1. Calendario: DatePickerDialog para filtrar por fecha e Intent ACTION_INSERT (CalendarContract) para agendar el vuelo en el calendario del dispositivo.
2. Correo (Intent ACTION_SENDTO) para la confirmación del vuelo.

## Código QR
El pase de abordaje genera un QR con ZXing (`util/GeneradorQr.kt`) con los datos del socio, el vuelo y el estado del consentimiento.

## Arquitectura (MVVM)
- `model/`: LoginModel, RegisterModel, VueloModel.
- `data/`: entidad, DAO y base de datos Room; datos de vuelos ficticios.
- `ui/viewmodel/`: Login, Register, Sesion (estado compartido) y Vuelos.
- `ui/screen/`, `ui/components/`, `ui/navigation/`, `ui/theme/`.
- `util/`: Validaciones, Seguridad y CorreoNativo.

## Cómo ejecutar
1. Abrir el proyecto en Android Studio y sincronizar Gradle.
2. Ejecutar en un emulador.
3. Registrar un socio, iniciar sesión, elegir y aceptar un vuelo.
4. Ver la base de datos en View > Tool Windows > App Inspection.

## Pendiente (entrega final)
- Usar la cámara para leer el QR (rol operador).
- Rol operador y conexión con backend.
- Guardar el vuelo aceptado y el consentimiento en Room.


## Envío real de correo
`util/CorreoSmtp.kt` envía la confirmación (con el QR adjunto) por SMTP de Gmail al aceptar el vuelo. Completa `REMITENTE` y `CLAVE_APP` (contraseña de aplicación de Google) en `util/ConfigCorreo.kt`. Si quedan vacíos, la app abre la app de correo del dispositivo como respaldo.
