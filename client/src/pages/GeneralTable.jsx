/* eslint-disable react/prop-types */
import React, { useState } from "react";
import { Button, Table } from "react-bootstrap";
import "../styles/styles.css";

import ClientesModal from "../components/ClientesModal";
import { Results } from "../components/Results";
import RKModal from "../components/RKModal";

const GeneralTable = ({ tabla }) => {
  const [modalShow, setModalShow] = useState(false);
  const [clientes, setClientes] = useState([]);
  const [rkShow, setRkShow] = useState(false);

  const handleShow = (clientes) => {
    setClientes(clientes);
    setModalShow(true);
  };

  const handleClose = () => {
    setClientes([]);
    setModalShow(false);
  };

  const estilos = {
    oscuro: { backgroundColor: "#ececec", verticalAlign: "middle" },
    claro: { backgroundColor: "#ffffff", verticalAlign: "middle" },
    peluquero1Info: { backgroundColor: "#ffbaba", verticalAlign: "middle" },
    peluquero2Info: { backgroundColor: "#d3ffba", verticalAlign: "middle" },
    peluquero3Info: { backgroundColor: "#bacaff", verticalAlign: "middle" },
  };

  const round = (num, decimales) => {
    if (num === null) return null;
    if (num === 0) return 0;
    return parseFloat(num).toFixed(decimales);
  };

  const getAttendantStyle = (nombreQuienAtiende) => {
    if (nombreQuienAtiende === "Aprendiz") {
      return estilos.peluquero1Info;
    } else if (nombreQuienAtiende === "Veterano A") {
      return estilos.peluquero2Info;
    } else if (nombreQuienAtiende === "Veterano B") {
      return estilos.peluquero3Info;
    } else {
      return estilos.oscuro;
    }
  };

  return (
    <>
      <Button
        onClick={() => {
          setRkShow(true);
        }}
        style={{ position: "absolute", left: "70px", top: "20px" }}
      >
        Mostrar RK
      </Button>
      <br />
      <br />
      <Table responsive striped bordered hover className="table-auto-width">
        <thead>
          <tr>
            <th rowSpan="3" style={estilos.oscuro}>
              i
            </th>
            <th rowSpan="3" style={estilos.claro}>
              Evento
            </th>

            <th colSpan="5" style={estilos.oscuro}>
              Relojes
            </th>

            <th colSpan="9" style={estilos.claro}>
              Variables Aleatorias
            </th>

            <th colSpan="9" style={estilos.oscuro}>
              Servidores
            </th>

            <th colSpan="4" style={estilos.claro}>
              Estadísticas
            </th>

            <th rowSpan="3" style={estilos.oscuro}>
              Clientes
            </th>
          </tr>
          <tr>
            <th rowSpan="2" style={estilos.oscuro}>
              Reloj Actual
            </th>
            <th rowSpan="2" style={estilos.oscuro}>
              Hora Actual
            </th>
            <th rowSpan="2" style={estilos.oscuro}>
              Dia
            </th>
            <th rowSpan="2" style={estilos.oscuro}>
              Reloj Total
            </th>
            <th rowSpan="2" style={estilos.oscuro}>
              Hora Total
            </th>

            <th colSpan="3" style={estilos.claro}>
              Llegada
            </th>

            <th colSpan="2" style={estilos.oscuro}>
              Quién atiende
            </th>

            <th colSpan="4" style={estilos.claro}>
              Tiempo Atención
            </th>

            <th colSpan="3" style={estilos.peluquero1Info}>
              APRENDIZ
            </th>
            <th colSpan="3" style={estilos.peluquero2Info}>
              VETERANO A
            </th>
            <th colSpan="3" style={estilos.peluquero3Info}>
              VETERANO B
            </th>

            <th rowSpan="3" style={estilos.claro}>
              Acumulador Costos
            </th>
            <th rowSpan="3" style={estilos.claro}>
              Acumulador Ganancias
            </th>
            <th rowSpan="3" style={estilos.claro}>
              Promedio Recaudacion Diaria
            </th>
            <th rowSpan="3" style={estilos.claro}>
              Sillas Necesarias
            </th>
          </tr>
          <tr>
            <th rowSpan="2" style={estilos.claro}>
              Random
            </th>
            <th rowSpan="2" style={estilos.claro}>
              Tiempo Entre Llegadas
            </th>
            <th rowSpan="2" style={estilos.claro}>
              Proxima Llegada
            </th>

            <th rowSpan="2" style={estilos.oscuro}>
              Random
            </th>
            <th rowSpan="2" style={estilos.oscuro}>
              Nombre
            </th>

            <th rowSpan="2" style={estilos.claro}>
              Random
            </th>
            <th rowSpan="2" style={estilos.claro}>
              Complejidad
            </th>
            <th rowSpan="2" style={estilos.claro}>
              Runge Kutta
            </th>
            <th rowSpan="2" style={estilos.claro}>
              Tiempo Atención
            </th>

            <th style={estilos.peluquero1Info}>Estado</th>
            <th style={estilos.peluquero1Info}>Cola</th>
            <th style={estilos.peluquero1Info}>Fin Atención</th>

            <th style={estilos.peluquero2Info}>Estado</th>
            <th style={estilos.peluquero2Info}>Cola</th>
            <th style={estilos.peluquero2Info}>Fin Atención</th>

            <th style={estilos.peluquero3Info}>Estado</th>
            <th style={estilos.peluquero3Info}>Cola</th>
            <th style={estilos.peluquero3Info}>Fin Atención</th>
          </tr>
        </thead>

        <tbody>
          {tabla.map((fila, index) => (
            <tr key={`body-${index}`}>
              <td style={estilos.oscuro}>{fila.iteracion}</td>
              <td style={{ ...estilos.claro, fontWeight: "bold" }}>
                {fila.evento}
              </td>
              <td style={{ ...estilos.oscuro, fontWeight: "bold" }}>
                {round(fila.relojActual, 4)}
              </td>
              <td style={estilos.oscuro}>{fila.horaActual + " hs"}</td>
              <td style={estilos.oscuro}>{"Día " + fila.dia}</td>
              <td style={estilos.oscuro}>{round(fila.relojTotal, 4)}</td>
              <td style={estilos.oscuro}>{fila.horaTotal + " hs"}</td>
              <td style={estilos.claro}>{round(fila.random1, 4)}</td>
              <td style={estilos.claro}>
                {round(fila.tiempoEntreLlegadas, 4)}
              </td>
              <td style={{ ...estilos.claro, fontWeight: "bold" }}>
                {round(fila.proximaLlegada, 4)}
              </td>
              <td style={estilos.oscuro}>{round(fila.random2, 4)}</td>
              <td style={getAttendantStyle(fila.nombreQuienAtiende)}>
                {fila.nombreQuienAtiende}
              </td>
              <td style={estilos.claro}>{round(fila.random3, 4)}</td>
              <td style={estilos.claro}>{round(fila.complejidad, 4)}</td>
              <td style={estilos.claro}>{round(fila.rungeKutta, 4)}</td>
              <td style={estilos.claro}>{round(fila.tiempoAtencion, 4)}</td>

              <td style={estilos.peluquero1Info}>
                {fila.peluqueros[0].estado}
              </td>
              <td style={estilos.peluquero1Info}>{fila.peluqueros[0].cola}</td>
              <td style={{ ...estilos.peluquero1Info, fontWeight: "bold" }}>
                {round(fila.peluqueros[0].finAtencion, 4)}
              </td>

              <td style={estilos.peluquero2Info}>
                {fila.peluqueros[1].estado}
              </td>
              <td style={estilos.peluquero2Info}>{fila.peluqueros[1].cola}</td>
              <td style={{ ...estilos.peluquero2Info, fontWeight: "bold" }}>
                {round(fila.peluqueros[1].finAtencion, 4)}
              </td>

              <td style={estilos.peluquero3Info}>
                {fila.peluqueros[2].estado}
              </td>
              <td style={estilos.peluquero3Info}>{fila.peluqueros[2].cola}</td>
              <td style={{ ...estilos.peluquero3Info, fontWeight: "bold" }}>
                {round(fila.peluqueros[2].finAtencion, 4)}
              </td>

              <td style={estilos.claro}>{"$" + fila.acumuladorCostos}</td>
              <td style={estilos.claro}>{"$" + fila.acumuladorGanancias}</td>
              <td style={estilos.claro}>
                {"$" + round(fila.promedioRecaudacionDiaria, 2)}
              </td>
              <td style={estilos.claro}>{fila.sillasNecesarias + " sillas"}</td>

              <td style={estilos.oscuro}>
                <Button
                  variant="link"
                  onClick={() => handleShow(fila.clientes)}
                >
                  VER MÁS
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>

      <br />
      <br />
      <Results
        cantSillas={tabla[tabla.length - 1].sillasNecesarias}
        prom={tabla[tabla.length - 1].promedioRecaudacionDiaria}
      />
      <ClientesModal
        show={modalShow}
        handleClose={handleClose}
        clientes={clientes}
      />
      <RKModal show={rkShow} handleClose={() => setRkShow(false)} />
    </>
  );
};

export { GeneralTable };
