export const API_URL = "http://localhost:8080/colas/simular";

export const example = [
    {
        evento: "Llegada del cliente 2",
        relojTotal: 14.106268,
        horaTotal: 1,
        relojDia: 14.106268,
        horaDia: 1,
        dia: 1,
        random1: 0.0141244605,
        tiempoEntreLlegadas: 2.1412446,
        proximaLlegada: 16.247513,
        random2: 0.4160291,
        nombreQuienAtiende: "Veterano A",
        random3: null,
        tiempoAtencion: null,
        peluqueros: [
            {
                nombre: "Aprendiz",
                estado: "LIBRE",
                cola: 0,
                finAtencion: null
            },
            {
                nombre: "Veterano A",
                estado: "OCUPADO",
                cola: 1,
                finAtencion: 22.039646
            },
            {
                nombre: "Veterano B",
                estado: "LIBRE",
                cola: 0,
                finAtencion: null
            }
        ],
        acumuladorCostos: 0.0,
        acumuladorGanancias: 0.0,
        promedioRecaudacionDiaria: 0.0,
        sillasNecesarias: 1,
        clientes: [
            {
                id: 1,
                estado: "SIENDO_ATENDIDO",
                acumuladorTiempoEspera: 0.0,
                peluquero: {
                    nombre: "Veterano A"
                }
            },
            {
                id: 2,
                estado: "ESPERANDO_ATENCION",
                acumuladorTiempoEspera: 0.0,
                peluquero: {
                    nombre: "Veterano A"
                }
            }
        ]
    }
];
