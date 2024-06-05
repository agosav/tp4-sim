/* eslint-disable react/prop-types */
import React, { useState } from "react";
import { Button, Table } from "react-bootstrap";
import ClientesModal from "../components/ClientesModal";
import { Results } from "../components/Results";

const GeneralTable = ({ tabla }) => {
  const [modalShow, setModalShow] = useState(false);
  const [clientes, setClientes] = useState([]);

  const handleShow = (clientes) => {
    setClientes(clientes);
    setModalShow(true);
  };

  const handleClose = () => {
    setClientes([]);
    setModalShow(false);
  };

  const estilos = {
    eventInfo: { backgroundColor: "#ffffcc" },
    randomArrival: { backgroundColor: "#ccffcc" },
    randomAttendant: { backgroundColor: "#cce5ff" },
    randomTime: { backgroundColor: "#e6ccff" },
    peluquero1Info: { backgroundColor: "#ffccff" },
    peluquero2Info: { backgroundColor: "#ffcccc" },
    peluquero3Info: { backgroundColor: "#ffcc99" },
    financialInfo: { backgroundColor: "#ffccff" },
  };

  return (
    <>
      <Table responsive striped bordered hover>
        <thead>
          <tr>
            <th style={estilos.eventInfo}>Evento</th>
            <th style={estilos.eventInfo}>Reloj Total</th>
            <th style={estilos.eventInfo}>Reloj Dia</th>
            <th style={estilos.eventInfo}>Hora</th>
            <th style={estilos.eventInfo}>Dia</th>
            <th style={estilos.randomArrival}>Random 1</th>
            <th style={estilos.randomArrival}>Tiempo Entre Llegadas</th>
            <th style={estilos.randomArrival}>Proxima Llegada</th>
            <th style={estilos.randomAttendant}>Random 2</th>
            <th style={estilos.randomAttendant}>Nombre Quien Atiende</th>
            <th style={estilos.randomTime}>Random 3</th>
            <th style={estilos.randomTime}>Tiempo Atencion</th>

            <th style={estilos.peluquero1Info}>Nombre Pel.1</th>
            <th style={estilos.peluquero1Info}>Estado Pel.1</th>
            <th style={estilos.peluquero1Info}>Cola Pel.1</th>
            <th style={estilos.peluquero1Info}>Fin Atención Pel.1</th>

            <th style={estilos.peluquero2Info}>Nombre Pel.2</th>
            <th style={estilos.peluquero2Info}>Estado Pel.2</th>
            <th style={estilos.peluquero2Info}>Cola Pel.2</th>
            <th style={estilos.peluquero2Info}>Fin Atención Pel.2</th>

            <th style={estilos.peluquero3Info}>Nombre Pel.3</th>
            <th style={estilos.peluquero3Info}>Estado Pel.3</th>
            <th style={estilos.peluquero3Info}>Cola Pel.3</th>
            <th style={estilos.peluquero3Info}>Fin Atención Pel.3</th>

            <th style={estilos.financialInfo}>Acumulador Costos</th>
            <th style={estilos.financialInfo}>Acumulador Ganancias</th>
            <th style={estilos.financialInfo}>Promedio Recaudacion Diaria</th>
            <th style={estilos.financialInfo}>Sillas Necesarias</th>

            <th>Clientes</th>
          </tr>
        </thead>

        <tbody>
          {tabla.map((fila, index) => (
            <tr key={`body-${index}`}>
              <td style={estilos.eventInfo}>{fila.evento}</td>
              <td style={estilos.eventInfo}>{fila.relojTotal}</td>
              <td style={estilos.eventInfo}>{fila.relojDia}</td>
              <td style={estilos.eventInfo}>{fila.hora}</td>
              <td style={estilos.eventInfo}>{fila.dia}</td>
              <td style={estilos.randomArrival}>{fila.random1}</td>
              <td style={estilos.randomArrival}>{fila.tiempoEntreLlegadas}</td>
              <td style={estilos.randomArrival}>{fila.proximaLlegada}</td>
              <td style={estilos.randomAttendant}>{fila.random2}</td>
              <td style={estilos.randomAttendant}>{fila.nombreQuienAtiende}</td>
              <td style={estilos.randomTime}>{fila.random3}</td>
              <td style={estilos.randomTime}>{fila.tiempoAtencion}</td>

              <td style={estilos.peluquero1Info}>
                {fila.peluqueros[0].nombre}
              </td>
              <td style={estilos.peluquero1Info}>
                {fila.peluqueros[0].estado}
              </td>
              <td style={estilos.peluquero1Info}>{fila.peluqueros[0].cola}</td>
              <td style={estilos.peluquero1Info}>
                {fila.peluqueros[0].finAtencion}
              </td>

              <td style={estilos.peluquero2Info}>
                {fila.peluqueros[1].nombre}
              </td>
              <td style={estilos.peluquero2Info}>
                {fila.peluqueros[1].estado}
              </td>
              <td style={estilos.peluquero2Info}>{fila.peluqueros[1].cola}</td>
              <td style={estilos.peluquero2Info}>
                {fila.peluqueros[1].finAtencion}
              </td>

              <td style={estilos.peluquero3Info}>
                {fila.peluqueros[2].nombre}
              </td>
              <td style={estilos.peluquero3Info}>
                {fila.peluqueros[2].estado}
              </td>
              <td style={estilos.peluquero3Info}>{fila.peluqueros[2].cola}</td>
              <td style={estilos.peluquero3Info}>
                {fila.peluqueros[2].finAtencion}
              </td>

              <td style={estilos.financialInfo}>{fila.acumuladorCostos}</td>
              <td style={estilos.financialInfo}>{fila.acumuladorGanancias}</td>
              <td style={estilos.financialInfo}>
                {fila.promedioRecaudacionDiaria}
              </td>
              <td style={estilos.financialInfo}>{fila.sillasNecesarias}</td>

              <td>
                <Button
                  variant="link"
                  onClick={() => handleShow(fila.clientes)}
                >
                  VER MAS
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
    </>
  );
};

export { GeneralTable };
